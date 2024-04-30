package uni.project.disco_orario_sveglia_20.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.databinding.FragmentCameraBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


//TODO: try on real device

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageUri.collect {
                viewModel.updateSelected(it)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        viewModel = (activity as ParkingDataActivity).parkingViewModel

        viewModel.updateNew(createImageUri())
        binding.captureButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.selectedImageUri.collect {
                    if (ActivityCompat.checkSelfPermission(
                            (activity as ParkingDataActivity),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        viewModel.getCameraPermission((activity as ParkingDataActivity))
                        contract.launch(it)
                    } else {
                        contract.launch(it)
                    }
                }
            }

        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        viewModel.imageUri.value?.let {
            binding.imageView.setImageURI(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()
    }


    private fun createImageUri(): Uri {
        val image = File((activity as ParkingDataActivity).filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            (activity as ParkingDataActivity),
            "uni.project.disco_orario_sveglia_20.FileProvider",
            image
        )
    }


}