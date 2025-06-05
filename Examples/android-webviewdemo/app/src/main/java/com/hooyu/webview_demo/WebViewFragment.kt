import WebViewFragmentArgs.*
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.hooyu.webview_demo.MainActivity
import com.hooyu.webview_demo.R
import com.hooyu.webview_demo.databinding.FragmentWebviewBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// Web view implementation with option to browse files, open camera and open embedded camera UI
class WebViewFragment : Fragment(R.layout.fragment_webview) {

    /** AndroidX navigation arguments - pass URL */
    private val args: WebViewFragmentArgs by navArgs()

    private var imagePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraImagePath: String? = null
    // to handle social login popups
    private var mWebviewPop: WebView? = null
    private var builder: AlertDialog? = null // to open sub-pages in popup

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI - hide logo and toolbar
        val mainActivity = requireActivity() as MainActivity
        mainActivity.hideLogo()
        mainActivity.hideToolbar()

        // open links in web view not in browser
        binding.hooyuWebView.setWebViewClient(getCustomWebClient())

        // configure webview
        binding.hooyuWebView.apply {
            settings.userAgentString = USER_AGENT
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.setSupportZoom(true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            webChromeClient = getCustomWebChromeClient()

            loadUrl(args.url)
        }
    }

    // to be able to close opened popup webviews (for social login). If URL is in hooyu.com domain we just close popup
    private fun getCustomWebClient() = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Uri.parse(url).host?.let {
                if (it.endsWith("hooyu.com") || it.endsWith("miteksystems.com")) {  // close popup when goes back to hooyu page
                    // do not close legal and terms pages
                    val legalPage =
                        url?.endsWith("legal/privacy-policy") == true || url?.endsWith("legal/terms-and-conditions") == true

                    if (!legalPage) {
                        // if opened - if show as full window
                        mWebviewPop?.let {
                            it.setVisibility(View.GONE)
                            binding.webviewFrame.removeView(mWebviewPop)
                            mWebviewPop = null
                        }

                        // close popup webview if opened as popup
                        builder?.let { it.dismiss() }
                    }
                }
            }

            return false
        }
    }

    // add custom client to allow camera/device browsing and upload files from web view,
    // also allows to open external links (like social login)
    private fun getCustomWebChromeClient() = object : WebChromeClient() {

        // new window events will be opened in a new webview - social login popups
        @SuppressLint("SetJavaScriptEnabled")
        override fun onCreateWindow(
            view: WebView?, isDialog: Boolean,
            isUserGesture: Boolean, resultMsg: Message
        ): Boolean {
            mWebviewPop = WebView(requireContext())
            mWebviewPop!!.isVerticalScrollBarEnabled = false
            mWebviewPop!!.isHorizontalScrollBarEnabled = false
            mWebviewPop!!.webViewClient = getCustomWebClient()
            mWebviewPop!!.settings.userAgentString = USER_AGENT
            mWebviewPop!!.settings.javaScriptEnabled = true;
            mWebviewPop!!.settings.javaScriptCanOpenWindowsAutomatically = true
            mWebviewPop!!.settings.setSupportMultipleWindows(true)

            // add popup webview on top
//            mWebviewPop!!.setLayoutParams(
//                FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//            )
//            webview_frame.addView(mWebviewPop)

            // add webview as popup
            builder = AlertDialog.Builder(requireContext(), AlertDialog.BUTTON_POSITIVE).create()
            builder!!.setTitle("")
            builder!!.setView(mWebviewPop)
            builder!!.setButton(AlertDialog.BUTTON_POSITIVE, "Close") { dialog: DialogInterface, id: Int ->
                mWebviewPop!!.destroy()
                dialog.dismiss()
            }
            builder!!.show()
            builder!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

            val transport = resultMsg.obj as WebView.WebViewTransport
            transport.webView = mWebviewPop
            resultMsg.sendToTarget()

            return true
        }

        override fun onCloseWindow(window: WebView?) {
            Log.d("Hooyu", "onCloseWindow called")
        }

        // allow permission for liveness camera
        override fun onPermissionRequest(request: PermissionRequest) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                with(request) { grant(resources) }
            }
        }

        // allow file upload/capture image
        override fun onShowFileChooser(
            view: WebView?,
            filePath: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }

            imagePathCallback?.onReceiveValue(null)
            imagePathCallback = null
            imagePathCallback = filePath

            val takePictureIntent = createImageCaptureIntent()

            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = INTENT_FILE_TYPE

            val intentArray: Array<Intent?>
            intentArray = arrayOf(takePictureIntent)

            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose file")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

            try {
                startActivityForResult(chooserIntent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                imagePathCallback = null
                cameraImagePath = null
                return false
            }

            return true
        }

        private fun createImageCaptureIntent(): Intent? {
            var captureImageIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (captureImageIntent?.resolveActivity(requireContext().
                packageManager) != null) {
                var imageFile: File? = null

                try {
                    imageFile = createImageFile()
                    captureImageIntent.putExtra("CameraImagePath", cameraImagePath)
                } catch (ex: IOException) {
                    ex.printStackTrace() // handle error
                }

                if (imageFile != null) {
                    cameraImagePath = CAMERA_PHOTO_PATH_POSTFIX + imageFile.absolutePath
                    captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
                } else {
                    captureImageIntent = null
                }
            }

            return captureImageIntent
        }

        private fun createImageFile(): File? {
            val timeStamp = SimpleDateFormat.getDateInstance().format(Date())
            val imageFileName = PHOTO_NAME_POSTFIX + timeStamp + "_"
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            return File.createTempFile(imageFileName, PHOTO_FORMAT, storageDir)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_SELECT_FILE || imagePathCallback == null) {return}

        var results: Array<Uri>? = null
        if (resultCode == RESULT_OK) {
            if (data == null) {
                if (cameraImagePath != null) results = arrayOf(Uri.parse(cameraImagePath))
            } else {
                val dataString = data.dataString
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }

        imagePathCallback?.onReceiveValue(results)
        imagePathCallback = null
    }

    companion object {
        // detect as a browser and show "browse from this device" button on document screens
        //private const val USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.2 (KHTML, like Gecko) Chrome/4.0.221.6 Safari/532.2"

        // detect as mobile device and show camera button on document screens
        private const val USER_AGENT = "Android"

        private const val CAMERA_REQUEST_CODE = 113
        private const val REQUEST_SELECT_FILE = 13
        private const val INTENT_FILE_TYPE = "image/*"
        private const val CAMERA_PHOTO_PATH_POSTFIX = "file:"
        private const val PHOTO_NAME_POSTFIX = "JPEG_"
        private const val PHOTO_FORMAT = ".jpg"
    }
}