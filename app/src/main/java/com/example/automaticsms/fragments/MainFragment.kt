package com.example.automaticsms.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.automaticsms.MainActivity
import com.example.automaticsms.databinding.FragmentMainBinding
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainFragment : Fragment() {
    private var isScannerInstalled = false
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        installGoogleScanner()
        init()
        registerUiListener()
        val systemDetails = (activity as MainActivity).getSystemDetails()
        val systemString = "Brand: ${systemDetails.brand} \n" +
                "DeviceID: ${systemDetails.deviceId} \n" +
                "Model: ${systemDetails.model} \n" +
                "ID: ${systemDetails.id} \n" +
                "SDK: ${systemDetails.sdk} \n" +
                "Manufacturer: ${systemDetails.manufacturer} \n" +
                "User: ${systemDetails.user} \n" +
                "Type: ${systemDetails.type} \n" +
                "Base: ${systemDetails.base} \n" +
                "Incremental: ${systemDetails.incremental} \n" +
                "Board: ${systemDetails.board} \n" +
                "Host: ${systemDetails.host} \n" +
                "FingerPrint: ${systemDetails.fingerPrint} \n" +
                "Version Code: ${systemDetails.versionCode} \n" +
                "IMEI: ${systemDetails.imei} \n" +
                "Operator: ${systemDetails.operatorName} \n" +
                "Charging: ${systemDetails.charging} \n" +
                "USB Charge: ${systemDetails.usbCharge} \n" +
                "AC Charge: ${systemDetails.acCharge} \n" +
                "Battery: ${systemDetails.batteryPct} \n"
        binding.tvInfo.text = systemString
    }

    private fun installGoogleScanner() {
        val moduleInstall = activity?.let { ModuleInstall.getClient(it) }
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(requireActivity()))
            .build()
        moduleInstall?.installModules(moduleInstallRequest)?.addOnSuccessListener {
            isScannerInstalled = true
        }?.addOnFailureListener {
            isScannerInstalled = false
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun init() {
        val options = initializeGoogleScanner()
        scanner = (activity as MainActivity).let { GmsBarcodeScanning.getClient(it, options) }!!
    }

    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom().build()
    }

    private fun registerUiListener() {
        binding.btScan.setOnClickListener {
            if (isScannerInstalled) {
                startScanning()
            } else {
                Toast.makeText(activity as MainActivity, "Попробуйте снова...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun startScanning() {
        scanner.startScan().addOnSuccessListener {
            val result = it.rawValue
            result?.let {
                Toast.makeText(activity as MainActivity, "$it", Toast.LENGTH_LONG).show()
                // Пока не знаю, куда это отправлять
            }
        }.addOnCanceledListener {
            Toast.makeText(activity as MainActivity, "Отменено", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(activity as MainActivity, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}