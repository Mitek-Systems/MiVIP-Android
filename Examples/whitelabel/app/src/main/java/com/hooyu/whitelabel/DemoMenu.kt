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
import com.hooyu.whitelabel.databinding.FragmentMenuBinding

class DemoMenu : Fragment(R.layout.fragment_menu) {

    private val mivipActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        MiVIPActivity.Result.result?.let { res ->
            res.scoreResult?.let {
                Log.i("MIVIP", "Score result $it")
            }
            res.request?.let {
                Log.i("MIVIP", "Resuest $it")
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

        // Scan QR code
        binding.btnScanQr.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_QR) // go to QR screen
                putExtra(MiVIPActivity.SOUNDS_DISABLED, true) // default is False
                putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // default is False
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
//                putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
            }
            mivipActivityResult.launch(intent)
        }

        // Open request with given MiVIP request ID
        binding.btnRequest.setOnClickListener {
            val intent = Intent(requireActivity(), MiVIPActivity::class.java).apply {
                putExtra(MiVIPActivity.SDK_FLAG, true) // mark we are in SDK mode
                putExtra(MiVIPActivity.ACTION_FLAG, MiVIPActivity.ACTION_REQUEST) // open request
                val mivipRequestId = "8ec4dd13-ad90-4176-ba77-f57770af291d"
                putExtra(MiVIPActivity.MIVIP_REQUEST_ID, mivipRequestId) // ID request
//                putExtra(MiVIPActivity.DOCUMENT_CALLBACK_URL, docCallbackUrl) // if want to receive server callback at document processing
                putExtra(MiVIPActivity.SOUNDS_DISABLED, false) // this is the default value (sounds on)
                putExtra(MiVIPActivity.REUSABLE_ENABLED, false) // this is the default value (wallet off)
                putExtra(MiVIPActivity.ENABLE_SCREENSHOTS, true) // default is false
            }
            mivipActivityResult.launch(intent)
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