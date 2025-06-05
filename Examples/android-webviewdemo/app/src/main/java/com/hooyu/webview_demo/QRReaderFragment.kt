import QRReaderFragmentDirections.*
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.barcode.Barcode

// scan QR code
class QRReaderFragment : com.hooyu.android.core.barcode.BarcodeReaderFragment(), com.hooyu.android.core.barcode.BarcodeReaderFragment.BarcodeReaderListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAutoFocus(true)
        setUseFlash(false)
        setListener(this)
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?) {
    }

    override fun onScannedMultiple(barcodes: List<Barcode?>?) {
        barcodes?.get(0)?.let { openWebView(it) }
    }

    override fun onCameraPermissionDenied() {
        Toast.makeText(requireContext(), "Camera permission denied!", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    override fun onScanned(barcode: Barcode?) {
        barcode?.let { openWebView(it) }
    }

    override fun onScanError(errorMessage: String?) {
        Toast.makeText(requireContext(), "QR code scan error!", Toast.LENGTH_SHORT).show()
    }

    private fun openWebView(barcode: Barcode) {
        Uri.parse(barcode.rawValue)?.let { url ->
            url.getQueryParameter("idRequest")?.let { idRequest ->
                val host = url.host
                if (host?.endsWith("hooyu.com") == true || host?.endsWith("miteksystems.com") == true) {
                    val lang = "en" //url.getQueryParameter("l")?.toString()?
                    val hooyuUrl = "https://$host/$lang/checkid/request/$idRequest"
                    requireActivity().runOnUiThread {
                        findNavController().navigate(
                            QRReaderFragmentDirections.qrcodeReaderToWebview(
                                url = hooyuUrl
                            )
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Not a MiVIP URL", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}