package mx.itson.edu.keynote

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioRecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class AudioRecordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recorder: MediaRecorder? = null
    var recording = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*recorder = MediaRecorder()*/
        requestPermissions()
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_audio_record, container, false)

        val grabarBtn: ImageButton = myFragmentView!!.findViewById(R.id.record_button)
        val grabarlbl: TextView = myFragmentView!!.findViewById(R.id.grabarlbl)
        grabarBtn.setOnClickListener{
            if (recording) {
                stopRecording()
                grabarlbl.setText("Grabar")
                grabarlbl.setTextColor(resources.getColor(R.color.grayDark))
                recording = false
            } else {
                startRecording()
                grabarlbl.setText("Grabando...")
                grabarlbl.setTextColor(resources.getColor(R.color.recordRed))
                recording = true
            }
        }
        // Inflate the layout for this fragment
        return myFragmentView
    }

    private fun startRecording() {
        try {
            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val downloadsDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Crear un archivo dentro de la carpeta pública de descargas
            val audioFile = File(downloadsDir, "miAudio.3gpp")
            audioFile.createNewFile()
            // Start recording
            recorder?.setOutputFile(audioFile.absolutePath)
            recorder?.prepare()
            recorder?.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
            e.printStackTrace()
        } catch (e: RuntimeException) {
            Log.e("LOG_TAG", "startRecording() failed")
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if(recorder != null){
            recorder?.stop();     // stop recording
            recorder?.reset();    // set state to idle
            recorder?.release();  // release resources back to the system
            recorder = null;
        }

    }

    private fun requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        val requestCode = 123 // any integer value you choose

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            /*recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val downloadsDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)*/

            // Crear un archivo dentro de la carpeta pública de descargas

            // Crear un archivo dentro de la carpeta pública de descargas
            /*val audioFile = File(downloadsDir, "miAudio.3gpp")
            audioFile.createNewFile()
            recorder?.setOutputFile(audioFile.absolutePath)*/
        } else {
            // Permissions not granted, request them from the user
            ActivityCompat.requestPermissions(requireActivity(), permissions, requestCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestRecordAudioPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRecording()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording

            } else {
                // Permission denied, show a message to the user
                Toast.makeText(requireContext(), "Permission denied to record audio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                val downloadsDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                // Crear un archivo dentro de la carpeta pública de descargas

                // Crear un archivo dentro de la carpeta pública de descargas
                val audioFile = File(downloadsDir, "miAudio.3gpp")

                recorder?.setOutputFile(audioFile.absolutePath)
            } else {
                print("Holaaaaaaaaaaa ea aea eaeae")
            }
        }
    }*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AudioRecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AudioRecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}