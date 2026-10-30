package com.example.tp_listview

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // 1) Les données : une liste MODIFIABLE de chaînes simples
    private val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami"
    )

    // 2) L'adaptateur fait le lien entre les données et la ListView
    private lateinit var adapter: ArrayAdapter<String>

    // 3) Les vues
    private lateinit var etContact: EditText
    private lateinit var btnModifier: Button
    private lateinit var btnAjouter: Button
    private lateinit var lvContacts: ListView

    // Position de l'élément sélectionné (-1 = aucune sélection)
    private var positionSelectionnee = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Gestion des Insets pour l'affichage bord à bord (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 16, systemBars.top + 16, systemBars.right + 16, systemBars.bottom + 16)
            insets
        }

        // Récupération des vues
        etContact = findViewById(R.id.etContact)
        btnModifier = findViewById(R.id.btnModifier)
        btnAjouter = findViewById(R.id.btnAjouter)
        lvContacts = findViewById(R.id.lvContacts)

        // Initialisation de la liste (triée par défaut)
        contacts.sort()

        // Création de l'adaptateur
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )
        lvContacts.adapter = adapter

        // Clic sur un élément : copier dans l'EditText
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            positionSelectionnee = position
            lvContacts.setItemChecked(position, true)
            etContact.setText(contacts[position])
            etContact.setSelection(etContact.text.length)
            etContact.requestFocus()
        }

        // Appui long : Supprimer (Exercice 2)
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->
            afficherDialogueSuppression(position)
            true
        }

        // Bouton Modifier
        btnModifier.setOnClickListener { modifierContact() }

        // Bouton Ajouter (Exercice 1)
        btnAjouter.setOnClickListener { ajouterContact() }

        // Touche "Terminé" du clavier
        etContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (positionSelectionnee == -1) ajouterContact() else modifierContact()
                true
            } else {
                false
            }
        }
    }

    /**
     * Ajoute un nouveau contact à la liste (Exercice 1 & 3 & 4)
     */
    private fun ajouterContact() {
        val nouveauNom = etContact.text.toString().trim()

        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Vérification des doublons (Exercice 3)
        if (contacts.contains(nouveauNom)) {
            Toast.makeText(this, R.string.msg_doublon, Toast.LENGTH_SHORT).show()
            return
        }

        contacts.add(nouveauNom)
        finaliserAction("Contact ajouté : $nouveauNom")
    }

    /**
     * Modifie le contact sélectionné (Exercice 3 & 4)
     */
    private fun modifierContact() {
        if (positionSelectionnee == -1) {
            Toast.makeText(this, R.string.msg_aucune_selection, Toast.LENGTH_SHORT).show()
            return
        }

        val nouveauNom = etContact.text.toString().trim()
        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Vérification des doublons (Exercice 3)
        if (contacts.contains(nouveauNom) && contacts[positionSelectionnee] != nouveauNom) {
            Toast.makeText(this, R.string.msg_doublon, Toast.LENGTH_SHORT).show()
            return
        }

        contacts[positionSelectionnee] = nouveauNom
        finaliserAction("Contact modifié : $nouveauNom")
    }

    /**
     * Supprime un contact après confirmation (Exercice 2)
     */
    private fun afficherDialogueSuppression(position: Int) {
        val nom = contacts[position]
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_supprimer_titre)
            .setMessage(getString(R.string.dialog_supprimer_message, nom))
            .setPositiveButton(R.string.dialog_oui) { _, _ ->
                contacts.removeAt(position)
                if (positionSelectionnee == position) {
                    positionSelectionnee = -1
                    etContact.text.clear()
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Contact supprimé", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.dialog_non, null)
            .show()
    }

    /**
     * Trie la liste, notifie l'adaptateur et réinitialise les champs (Exercice 4)
     */
    private fun finaliserAction(message: String) {
        contacts.sort() // Tri automatique (Exercice 4)
        adapter.notifyDataSetChanged()
        
        etContact.text.clear()
        positionSelectionnee = -1
        lvContacts.clearChoices()
        lvContacts.requestLayout()
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
