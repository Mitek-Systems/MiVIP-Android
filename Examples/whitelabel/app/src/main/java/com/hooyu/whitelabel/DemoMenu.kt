package com.hooyu.whitelabel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.hooyu.android.MiVIPActivity
import com.hooyu.android.extensions.hooyuService
import com.hooyu.android.extensions.mivipService
import com.hooyu.android.utils.SettingsUtils
import com.hooyu.whitelabel.databinding.FragmentMenuBinding
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment
import com.miteksystems.misnap.workflow.fragment.FaceAnalysisFragment // why not found?
import com.miteksystems.misnap.workflow.fragment.HelpFragment
import com.miteksystems.misnap.workflow.fragment.NfcReaderFragment
import com.miteksystems.misnap.workflow.fragment.ReviewFragment
import com.miteksystems.misnap.workflow.fragment.VoicePhraseSelectionFragment
import com.miteksystems.misnap.workflow.fragment.VoiceProcessorFragment
import java.util.UUID

class DemoMenu : Fragment(R.layout.fragment_menu) {

    private val mivipActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        MiVIPActivity.Result.result?.let { res ->
            res.scoreResult?.let {
                Log.i("MIVIP", "Score result: $it")
            }
            res.request?.let {
                Log.i("MIVIP", "Resuest: $it")
            }
            res.error?.let {
                Log.i("MIVIP", "Error: $it")
            }
        }
        MiVIPActivity.Result.clearResult()
    }

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val miVipVersion = MiVIPActivity.VERSION
        Log.i("MIVIP ", "Version: $miVipVersion")
        binding.sdkVersion.text = "Version: $miVipVersion"

        // MiSnap customisation settings
        val faceAnalysisFragmentWorkflowSettings =
            FaceAnalysisFragment.buildWorkflowSettings(
                showCountdownTimer = true,
                guideViewShowVignette = true,
                //guideViewDrawableId = android.R.drawable.ic_menu_view,
                reviewCondition = FaceAnalysisFragment.ReviewCondition.NEVER
            )
        val helpFragmentFacetWorkflowSettings =
            HelpFragment.buildWorkflowSettings(showSkipCheckBox = false)
        val reviewFragmenFaceWorkflowSettings = ReviewFragment.buildWorkflowSettings()

        val documentAnalysisFragmentWorkflowSettings =
            DocumentAnalysisFragment.buildWorkflowSettings(
                timeoutDuration = 15_000,
                manualButtonDrawableId = android.R.drawable.ic_menu_camera,
                guideViewShowVignette = true,
                hintViewShouldShowBackground = true,
                successViewShouldVibrate = true,
                reviewCondition = DocumentAnalysisFragment.ReviewCondition.ALWAYS
            )
        val helpFragmentDocumentWorkflowSettings =
            HelpFragment.buildWorkflowSettings(showSkipCheckBox = false)
        val reviewFragmenDocumentWorkflowSettings = ReviewFragment.buildWorkflowSettings()

        val nfcReaderFragmentWorkflowSettings = NfcReaderFragment.buildWorkflowSettings()
        val voicePhraseSelectionFragmentWorkflowSettings = VoicePhraseSelectionFragment.buildWorkflowSettings()
        val voiceProcessorFragmentWorkflowSettings = VoiceProcessorFragment.buildWorkflowSettings()
        // SDK actions:

        // Scan QR code
        binding.btnScanQr.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_QR) // go to QR screen
                putExtra(MiVIPActivity.SOUNDS_DISABLED, true) // default is False
                putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // default is False
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
                putExtra(MiVIPActivity.LOG_DISABLED, false) // set if to report failures to server - default is false (print server logs)
//                putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing

                // MiSnap passthrough configurations:
                putExtra(MiVIPActivity.MISNAP_helpFragmentFaceWorkflowSettings, helpFragmentFacetWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_reviewFragmentFaceWorkflowSettings, reviewFragmenFaceWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_faceAnaLysisFragmentWorkflowSettings, faceAnalysisFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_helpFragmentDocumentWorkflowSettings, helpFragmentDocumentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_reviewFragmentDocumentWorkflowSettings, reviewFragmenDocumentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_documentAnalysisFragmentWorkflowSettings, documentAnalysisFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_nfcReaderFragmentWorkflowSettings, nfcReaderFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_voicePhraseSelectionFragmentWorkflowSettings, voicePhraseSelectionFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_voiceProcessorFragmentWorkflowSettings, voiceProcessorFragmentWorkflowSettings)

            }
            mivipActivityResult.launch(intent)
        }

        // Open request with given MiVIP request ID
        binding.btnRequest.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_REQUEST) // open request
                val mivipRequestId = "22f550cf-c03f-4b62-aa59-e22df7b77233"
                putExtra(MiVIPActivity.MIVIP_REQUEST_ID, mivipRequestId) // ID request
//                putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
                putExtra(MiVIPActivity.SOUNDS_DISABLED, false) // this is the default value (sounds on)
                putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // this is the default value (wallet off)
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false

                // MiSnap passthrough configurations:
                putExtra(MiVIPActivity.MISNAP_helpFragmentFaceWorkflowSettings, helpFragmentFacetWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_reviewFragmentFaceWorkflowSettings, reviewFragmenFaceWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_faceAnaLysisFragmentWorkflowSettings, faceAnalysisFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_helpFragmentDocumentWorkflowSettings, helpFragmentDocumentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_reviewFragmentDocumentWorkflowSettings, reviewFragmenDocumentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_documentAnalysisFragmentWorkflowSettings, documentAnalysisFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_nfcReaderFragmentWorkflowSettings, nfcReaderFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_voicePhraseSelectionFragmentWorkflowSettings, voicePhraseSelectionFragmentWorkflowSettings)
                putExtra(MiVIPActivity.MISNAP_voiceProcessorFragmentWorkflowSettings, voiceProcessorFragmentWorkflowSettings)
            }
            mivipActivityResult.launch(intent)
        }

        // Open MiVIP request using 4 digit request code
        binding.btnCode.setOnClickListener {
            val code = "1234" // Code is shown in MiVIP 'install app' web page
            mivipService().verifyCode(code,
                onSuccess = { idRequest: UUID? ->
                    idRequest?.let {
                        // same like open request:
                        val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                            putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                            putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_REQUEST) // open request
                            val mivipRequestId = idRequest
                            putExtra(MiVIPActivity.MIVIP_REQUEST_ID, mivipRequestId) // ID request
                            // putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
                            putExtra(MiVIPActivity.SOUNDS_DISABLED, false) // this is the default value (sounds on)
                            putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // this is the default value (wallet off)
                            putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false

                            // MiSnap passthrough configurations:
                            putExtra(MiVIPActivity.MISNAP_helpFragmentFaceWorkflowSettings, helpFragmentFacetWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_reviewFragmentFaceWorkflowSettings, reviewFragmenFaceWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_faceAnaLysisFragmentWorkflowSettings, faceAnalysisFragmentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_helpFragmentDocumentWorkflowSettings, helpFragmentDocumentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_reviewFragmentDocumentWorkflowSettings, reviewFragmenDocumentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_documentAnalysisFragmentWorkflowSettings, documentAnalysisFragmentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_nfcReaderFragmentWorkflowSettings, nfcReaderFragmentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_voicePhraseSelectionFragmentWorkflowSettings, voicePhraseSelectionFragmentWorkflowSettings)
                            putExtra(MiVIPActivity.MISNAP_voiceProcessorFragmentWorkflowSettings, voiceProcessorFragmentWorkflowSettings)
                        }
                        mivipActivityResult.launch(intent)
                    }
                },
                onFailure = { errCode: Int?, message: String?, errorBody: String?, e: Exception? ->
                    // Something went wrong
                }
            )
        }

        // Show history
        binding.btnHistory.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_HISTORY) // go to history screen
                putExtra(MiVIPActivity.SOUNDS_DISABLED, false) // this is the default value
                putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // this is the default value
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
            }
            startActivity(intent)
        }

        // Show user account
        binding.btnAccount.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_ACCOUNT) // go to account screen
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
            }
            startActivity(intent)
        }

    }

}