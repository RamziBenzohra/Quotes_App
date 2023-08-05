package com.app.quote.app.quotesapp.Client.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.app.quote.app.quotesapp.R
import com.app.quote.app.quotesapp.Client.models.Profile
import com.app.quote.app.quotesapp.Client.models.Qoutes_firebase
import com.google.firebase.firestore.*

/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {
    private lateinit var FirstIntro:TextView
    private lateinit var SecondIntro:TextView
    private lateinit var WebSite:TextView
    private lateinit var Phone:TextView
    private lateinit var Email:TextView
    private lateinit var Facebook:TextView
    private lateinit var Instegram:TextView
    private lateinit var Twitter:TextView
    private lateinit var Pinterest:TextView
    private lateinit var Youtube:TextView
    private val mfirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_info, container, false)
        FirstIntro=view.findViewById(R.id.first_info)
        SecondIntro=view.findViewById(R.id.second_info)
        WebSite=view.findViewById(R.id.website_info)
        Phone=view.findViewById(R.id.phone_info)
        Email=view.findViewById(R.id.email_info)
        Facebook=view.findViewById(R.id.face_info)
        Instegram=view.findViewById(R.id.inst_info)
        Twitter=view.findViewById(R.id.twit_info)
        Pinterest=view.findViewById(R.id.pintr_info)
        Youtube=view.findViewById(R.id.you_info)
        Add_Cheched_Listener{
            it.indexOfFirst {
                FirstIntro.text=it.FirstIntro
                SecondIntro.text=it.SecondIntro
                WebSite.text=it.WebSite
                Phone.text=it.Phone
                Email.text=it.Email
                Facebook.text=it.Facebook
                Instegram.text=it.Instegram
                Twitter.text=it.Twitter
                Pinterest.text=it.Pinterest
                Youtube.text=it.Youtube
                return@indexOfFirst true
            }

        }
        return view
    }
    private fun Add_Cheched_Listener(getitems:(ArrayList<Profile>)->Unit): ListenerRegistration {
        val query=mfirestore.collection("Profile").document("Profile")
        return query.addSnapshotListener{ documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException!=null){
                return@addSnapshotListener
            }else{
                var profile=ArrayList<Profile>()
                Log.d("checked listener",documentSnapshot!!.exists().toString())
                if( documentSnapshot.exists()){
                profile.add(documentSnapshot.toObject(Profile::class.java)!!)
                getitems(profile)
                }
            }



        }
    }
}
