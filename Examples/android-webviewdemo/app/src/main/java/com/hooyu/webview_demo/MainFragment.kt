
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hooyu.webview_demo.MainActivity
import com.hooyu.webview_demo.R
import com.hooyu.webview_demo.databinding.FragmentMainBinding
import MainFragmentDirections.*

// Main app screen
class MainFragment : Fragment(R.layout.fragment_main) {


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.showLogo()
        mainActivity.showToolbar()

        // open QR code fragment
        binding.btnMainQr.actionButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToQr())
        }

        // open webview fragment
        binding.btnMainUrl.actionButton.setOnClickListener {
            binding.urlInput.text?.toString()?.let {
                Uri.parse(it)?.let {url ->
                    val host = url.host
                    if (host?.endsWith("hooyu.com") == true || host?.endsWith("miteksystems.com") == true) {
                        findNavController().navigate(MainFragmentDirections.mainToWebview(url = it))
                    } else {
                        Toast.makeText(requireContext(), "Not a MiVIP URL", Toast.LENGTH_LONG).show()
                        binding.urlInput.text.clear()
                    }
                }
            }
        }

    }

}