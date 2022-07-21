package edu.msudenver.project2_final

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 02 - CreateUpdateActivity (controller) class
 */

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.util.*

class CreateUpdateActivity : AppCompatActivity(), View.OnClickListener {

    var op = CREATE_OP
    var id = 0
    lateinit var db: SQLiteDatabase
    lateinit var edtDescription: EditText
    lateinit var spnStatus: Spinner

    companion object {
        const val CREATE_OP = 0
        const val UPDATE_OP = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_update)

        // TODO #8: get references to the view objects
        edtDescription = findViewById(R.id.txtDescription)
        spnStatus = findViewById(R.id.txtSpinner)

        // TODO #9: define the spinner's adapter as an ArrayAdapter of String
        spnStatus.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Item.STATUS_DESCRIPTIONS)


        // TODO #10: get a reference to the "CREATE/UPDATE" button and sets its listener
        val btnCreateUpdate: Button = findViewById(R.id.btnCreateUpdate)
        btnCreateUpdate.setOnClickListener(this)


        // TODO #11: get a "writable" db connection
        val dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase


        op = intent.getIntExtra("op", CREATE_OP)

        // TODO #12: set the button's text to "CREATE"; make sure the spinner's selection is Item.SCHEDULED and the spinner is not enabled
        if (op == CREATE_OP) {
            btnCreateUpdate.text = "CREATE"
            spnStatus.setSelection(Item.SCHEDULED)
            spnStatus.isEnabled = false

        }
        // TODO #13: set the button's text to "UPDATE"; extract the item's id from the intent; use retrieveItem to retrieve the item's info; use the info to update the description and status view components
        else {
            btnCreateUpdate.text = "UPDATE"
            val id = intent.getIntExtra("id",0)
            val item = retrieveItem(id)
            edtDescription.setText(item.description)
            spnStatus.setSelection(item.status)


        }
    }

    // TODO #14: return the item based on the given id
    // this function should query the database for the bucket list item identified by the given id; an item object should be returned
    fun retrieveItem(id: Int): Item {
        val cursor = db.query(
            "bucketlist",
            arrayOf("rowid", "description", "creation_date", "update_date", "status"),
            "rowid = \"${id}\"",
            null,
            null,
            null,
            null

        )
        with(cursor) {
            cursor.moveToNext()
            val description = cursor.getString(1)
            val create_date = DBHelper.ISO_FORMAT.parse(cursor.getString(2))
            val  update_date = DBHelper.ISO_FORMAT.parse(cursor.getString(3))
            val status = cursor.getInt(4)
            val item = Item(id, description, create_date!!, update_date!!, status)
            return item

        }

    }

    override fun onClick(view: View?) {

        // TODO #15: add a new item to the bucket list based on the information provided by the user
        // both created_date and update_date should be set to current's date (use ISO format)
        // status should be set to Item.SCHEDULED
        val id = intent.getIntExtra("id",0)
        val description: TextView = findViewById(R.id.txtDescription)
        val current = Date()
        val creation_date = DBHelper.ISO_FORMAT.format(current)
        val update_date = DBHelper.ISO_FORMAT.format(current)
        val status = findViewById<Spinner>(R.id.txtSpinner).selectedItemPosition
        if (op == CREATE_OP) {
            try {
                db.execSQL(
                    """
                        INSERT INTO bucketlist VALUES
                        (${id}, "${description}", "${creation_date}","${update_date}",${status} )
                    """
                )
                Toast.makeText(
                    this,
                    "New bucket list is successfully created!",
                    Toast.LENGTH_SHORT).show()

            } catch (ex: Exception) {
                print(ex.toString())
                Toast.makeText(
                    this,
                    "Exception when trying to create a new bucket list!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        // TODO #16: update the item identified by "id"
        // update_date should be set to current's date (use ISO format)
        else {
            try {
                db.execSQL(
                    """
                        UPDATE bucketlist SET
                        description = ${description},
                        creation_date = ${creation_date},
                        update_date = ${update_date},
                        status      = ${status}
                        WHERE rowid = ${id}
                    """
                )
                Toast.makeText(
                    this,
                    "Bucketlist is successfully updated!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch(ex: Exception) {
                print(ex.toString())
                Toast.makeText(this,
                "Exception when trying to update bucketlist!",
                Toast.LENGTH_SHORT).show()
            }

        }
        finish()
    }
}