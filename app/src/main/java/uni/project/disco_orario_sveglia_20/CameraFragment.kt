package uni.project.disco_orario_sveglia_20

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uni.project.disco_orario_sveglia_20.databinding.FragmentCameraBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService

    private var _imageUriFlow = MutableStateFlow<Uri?>(null)
    private val selectedImageUri = _imageUriFlow.asStateFlow()

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){

        viewModel.imageUri = selectedImageUri.value

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        viewModel = (activity as ParkingDataActivity).parkingViewModel

        _imageUriFlow.update { createImageUri() }
        binding.captureButton.setOnClickListener {
            contract.launch(selectedImageUri.value)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        viewModel.imageUri?.let {
            binding.imageView.setImageURI(it)
            val parentLayout = binding.textView5.parent as ViewGroup
            parentLayout.removeView(binding.textView5)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()
    }


    private fun createImageUri() : Uri {
        val image = File((activity as ParkingDataActivity).filesDir, "camera_photos.png")
        return FileProvider.getUriForFile((activity as ParkingDataActivity),
            "uni.project.disco_orario_sveglia_20.FileProvider",
            image)
    }


}