package uni.project.disco_orario_sveglia_20.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.databinding.FragmentCameraBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var loadingDialog: Dialog

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        viewModel = (activity as ParkingDataActivity).parkingViewModel

        binding.captureButton.setOnClickListener {
            if (viewModel.isCameraPermissionOk((activity as ParkingDataActivity)))
            {
                viewModel.deleteImageFile((activity as ParkingDataActivity))
                contract.launch(viewModel.createImageUri((activity as ParkingDataActivity)))
            } else {
                viewModel.deleteImageFile((activity as ParkingDataActivity))
                Toast.makeText((activity as ParkingDataActivity), R.string.camera_permission, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        binding.imageView.setImageDrawable(null)
        viewModel.getImageFile((activity as ParkingDataActivity))?.let {
            showLoadingDialog()
            binding.imageView.load(it) {
                listener(
                    onSuccess = { _, _ -> dismissLoadingDialog() },
                    onError = { _, _ -> dismissLoadingDialog() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contract.unregister()
        cameraExecutor.shutdown()
    }

    private fun showLoadingDialog() {
        loadingDialog = Dialog((activity as ParkingDataActivity))
        loadingDialog.apply {
            setContentView(R.layout.loading_dialog)
            setCancelable(false)
            show()
        }
    }

    private fun dismissLoadingDialog() {
        if ( loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }


}