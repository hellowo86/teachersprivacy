package com.hellowo.teamfinder.ui.fragment

import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.model.Parent
import com.hellowo.teachersprivacy.model.Student
import com.hellowo.teachersprivacy.showAlertDialog
import com.hellowo.teamfinder.ui.adapter.StudentAdapter
import com.hellowo.teamfinder.viewmodel.StudentViewModel
import com.nononsenseapps.filepicker.FilePickerActivity
import com.opencsv.CSVReader
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_student.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import android.support.v7.widget.RecyclerView




class StudentFragment : LifecycleFragment() {
    val RC_FILE = 1
    lateinit var viewModel: StudentViewModel
    lateinit var adapter: StudentAdapter
    lateinit var decoration: StickyRecyclerHeadersDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(StudentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_student, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StudentAdapter(activity, viewModel.studentList) {}
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        decoration = StickyRecyclerHeadersDecoration(adapter)
        recyclerView.addItemDecoration(decoration)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                decoration.invalidateHeaders()
            }
        })

        titleText.setOnClickListener { startFilePickerActivity() }
    }

    private fun startFilePickerActivity() {
        // This always works
        val i = Intent(activity, FilePickerActivity::class.java)
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE)

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().path)

        startActivityForResult(i, RC_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == RC_FILE && resultCode == Activity.RESULT_OK) {
            val uri = intent!!.data
            val file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri)
            insertDataToRealmFromCsvFile(activity, file)
        }
    }

    fun insertDataToRealmFromCsvFile(activity: Activity, file: File) {
        val realm = Realm.getDefaultInstance()
        try {
            val reader = CSVReader(InputStreamReader(FileInputStream(file), "UTF-8"))
            var number = 0

            realm.beginTransaction()

            do{
                val buf = reader.readNext()
                buf?.let {
                    if(buf[0].isNotEmpty() && buf[1].isNotEmpty()) {
                        val student = Student()
                        student.id = UUID.randomUUID().toString()
                        student.number = ++number
                        student.name = buf[0]
                        student.phoneNumber = buf[1]
                        student.address = buf[2]
                        if (buf.size > 3) {
                            val parent = Parent()
                            parent.phoneNumber = buf[3]
                            val parents = RealmList<Parent>()
                            parents.add(parent)
                            student.parents = parents
                        }
                        realm.copyToRealmOrUpdate(student)
                    }
                }
            }while (buf != null)

            realm.commitTransaction()
        } catch (e: NoClassDefFoundError) {
            realm.cancelTransaction()
            showAlertDialog(activity, activity.getString(R.string.error_read_csv_title), activity.getString(R.string.error_read_csv_message),
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }, null, 0 /* 아이콘 아이디 */)
        } catch (e: Exception) {
            realm.cancelTransaction()
            showAlertDialog(activity, activity.getString(R.string.error_read_csv_title), activity.getString(R.string.error_read_csv_message),
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }, null, 0)
        }

    }
}
