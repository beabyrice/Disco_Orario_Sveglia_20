package uni.project.disco_orario_sveglia_20.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.databinding.FragmentMyCarBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel


class MyCarFragment : Fragment(R.layout.fragment_my_car), OnMapReadyCallback {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentMyCarBinding
    private lateinit var mMap: GoogleMap

    companion object{
        private const val LOCATION_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding =FragmentMyCarBinding.bind(view)

        viewModel = (activity as ParkingDataActivity).parkingViewModel

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        val location = viewModel.getMyCarLocation()
        if(location!= null){
            placeMarkerOnMap(location)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions()
            .position(currentLatLong)
            .title(getString(R.string.get_direction))
            .icon(bitmapFromVector((activity as ParkingDataActivity), R.drawable.baseline_directions_car_filled_24))
        mMap.addMarker(markerOptions)
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            ?: throw IllegalArgumentException("Resource ID $vectorResId is not a valid vector drawable resource.")

        vectorDrawable.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).apply {
                draw(this)
            }
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}