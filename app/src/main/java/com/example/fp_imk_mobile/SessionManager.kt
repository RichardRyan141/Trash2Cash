package com.example.fp_imk_mobile

import android.util.Log
import com.example.fp_imk_mobile.data.Category
import com.example.fp_imk_mobile.data.Location
import com.example.fp_imk_mobile.data.Transaction
import com.example.fp_imk_mobile.data.TransactionDetail
import com.example.fp_imk_mobile.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserSessionManager {
    var loggedInUser: User? = null
    fun getUserData(uid: String, onResult: (User?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null && snapshot.key != null) {
                    user.id = snapshot.key!!
                    Log.d("FirebaseUser", "User: $user")
                    onResult(user)
                } else {
                    Log.w("FirebaseUser", "User data is null for UID: $uid")
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load user data", error.toException())
                onResult(null)
            }
        })
    }

    fun getUserDataFromPhone(noTelp: String, onResult: (User?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.orderByChild("noTelp").equalTo(noTelp)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        val userId = child.key
                        if (user != null && userId != null) {
                            user.id = userId
                            onResult(user)
                            return
                        }
                    }
                    onResult(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to retrieve user by phone: ${error.message}")
                    onResult(null)
                }
            })
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.get()
            .addOnSuccessListener { snapshot ->
                val usersList = mutableListOf<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    val userId = child.key
                    if (user != null && userId != null) {
                        user.id = userId
                        usersList.add(user)
                    }
                }
                onResult(usersList)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to load users: ${it.message}")
                onResult(emptyList())
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid == null) {
                        onResult(false, "User ID tidak ditemukan")
                        return@addOnCompleteListener
                    }

                    getUserData(uid) { fetchedUser ->
                        if (fetchedUser != null) {
                            if (fetchedUser.role == "user") {
                                loggedInUser = fetchedUser
                                onResult(true, null)
                            } else {
                                auth.signOut()
                                onResult(false, "Login ini hanya untuk user")
                            }
                        } else {
                            auth.signOut()
                            onResult(false, "Gagal memverifikasi role pengguna")
                        }
                    }
                } else {
                    onResult(false, "Login gagal: Email atau password salah")
                }
            }
    }

    fun registerUser(user: User, password: String, onResult: (Boolean, String?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val usersRef = database.getReference("users")

        usersRef.orderByChild("noTelp").equalTo(user.noTelp).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    onResult(false, "Nomor telepon sudah digunakan")
                } else {
                    auth.createUserWithEmailAndPassword(user.email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                val uid = firebaseUser?.uid ?: return@addOnCompleteListener
                                user.id = uid

                                usersRef.child(uid).setValue(user)
                                    .addOnSuccessListener {
                                        loggedInUser = user
                                        onResult(true, null)
                                    }
                                    .addOnFailureListener {
                                        onResult(false, "Gagal menyimpan data user")
                                    }
                            } else {
                                onResult(false, "Registrasi gagal: ${task.exception?.message}")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, "Gagal memeriksa nomor telepon: ${e.message}")
            }
    }

    fun editUser(user: User, onResult: (Boolean, String?) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.id)

        val updatedUser = mapOf(
            "username" to user.username,
            "email" to user.email,
            "noTelp" to user.noTelp,
            "role" to user.role,
            "balance" to user.balance,
            "lokasiId" to user.lokasiID
        )

        usersRef.updateChildren(updatedUser)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, "Gagal mengupdate data: ${exception.message}")
            }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}

object LocationSessionManager {
    fun getAllLocations(onSuccess: (List<Location>) -> Unit, onError: (DatabaseError) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("locations")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Location>()
                for (child in snapshot.children) {
                    val loc = child.getValue(Location::class.java)
                    if (loc != null) {
                        list.add(loc.copy(id = child.key))
                    }
                }
                onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }

    fun createLocation(loc: Location, onResult: (Boolean, String?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("locations")

        dbRef.orderByChild("namaLokasi").equalTo(loc.namaLokasi).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    onResult(false, "Nama lokasi sudah digunakan")
                } else {
                    val newRef = dbRef.push()
                    val locWithId = loc.copy(id = newRef.key)

                    newRef.setValue(locWithId)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Gagal menyimpan lokasi: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Query failed", e)
                onResult(false, "Gagal mengecek nama lokasi: ${e.message}")
            }
    }
}

object TransactionSessionManager {
    fun addTransaction(trans: Transaction, onResult: (Boolean, String?, String?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("transactions")

        val newRef = dbRef.push()
        newRef.setValue(trans)
            .addOnSuccessListener {
                onResult(true, null, newRef.key)
            }
            .addOnFailureListener { e ->
                onResult(false, "Gagal menyimpan transaksi: ${e.message}", null)
            }
    }

    fun getUserTransactionList(user_id: String,onSuccess: (List<Transaction>) -> Unit, onError: (String) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("transactions")
        dbRef.get()
            .addOnSuccessListener { snapshot ->
                val transactionsList = mutableListOf<Transaction>()
                for (child in snapshot.children) {
                    val trans = child.getValue(Transaction::class.java)
                    if (trans != null && (trans.tujuan == user_id || trans.user_id == user_id)) {
                        val transID = child.key
                        if (transID != null) {
                            transactionsList.add(trans.copy(noRef = transID))
                        }
                    }
                }
                onSuccess(transactionsList)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to retrieve transactions: ${it.message}")
                onError("${it.message}")
            }
    }
    fun getDetailsFor(transID: String, onResult: (List<TransactionDetail>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("transactionDetails").child(transID)

        dbRef.get()
            .addOnSuccessListener { snapshot ->
                val detailList = mutableListOf<TransactionDetail>()
                for (child in snapshot.children) {
                    val detail = child.getValue(TransactionDetail::class.java)
                    if (detail != null) {
                        detailList.add(detail)
                    }
                }
                onResult(detailList)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to get transaction details: ${e.message}")
                onResult(emptyList())
            }
    }

}

object CategorySessionManager {
    fun getCategoryList(onSuccess: (List<Category>) -> Unit, onError: (DatabaseError) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("categories")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (child in snapshot.children) {
                    val category = child.getValue(Category::class.java)
                    if (category != null) {
                        list.add(category.copy(id = child.key ?: ""))
                    }
                }
                onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }
}
