package com.example.examplestep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var universityName: String? = null
        private set

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadUserUniversity() // 뷰모델 초기화 시 사용자 대학명 로드
    }

    // 사용자의 대학명 불러오는 함수
    private fun loadUserUniversity() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                universityName = document.getString("university")
            }
            .addOnFailureListener { e ->
                // 예외 처리
                e.printStackTrace()
            }
    }

    // 오늘 날짜의 걸음 수를 Firestore에 업데이트하는 함수
    fun updateDailySteps(stepCount: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val universityName = universityName ?: return // 대학명을 로드한 후 사용
        val dailyStepsData = hashMapOf("stepCount" to stepCount)

        // 대학명과 사용자 ID를 기반으로 Firestore 경로 설정
        val dailyStepsRefUniversityCollection = db.collection("universities")
            .document(universityName)
            .collection("users")
            .document(userId)
            .collection("dailySteps")
            .document(today) // 날짜를 문서 ID로 사용

        // userId를 기반으로 경로 설정
        val dailyStepsRefUserCollection = db.collection("users")
            .document(userId)
            .collection("dailySteps")
            .document(today)

        // 각 경로에 맞게 get을 하고 날짜에 대한 데이터가 있으면 update()사용 없으면 set()사용해서 날짜에 맞는 데이터를 추가
        dailyStepsRefUniversityCollection.get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()){
                    dailyStepsRefUniversityCollection.update(dailyStepsData as Map<String, Any>)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener{e->
                            onFailure(e)
                        }
                }else{
                    dailyStepsRefUniversityCollection.set(dailyStepsData as Map<String, Any>)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener{e->
                            onFailure(e)
                        }
                }
            }

        dailyStepsRefUserCollection.get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()){
                    dailyStepsRefUserCollection.update(dailyStepsData as Map<String, Any>)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener{e->
                            onFailure(e)
                        }
                }else{
                    dailyStepsRefUserCollection.set(dailyStepsData as Map<String, Any>)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener{e->
                            onFailure(e)
                        }
                }
            }

//        db.collection("users").document(userId)
//            .collection("dailySteps").document(today)
//            .set(dailyStepsData)
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { e -> onFailure(e) }

//        db.collection("universities").document(universityName)
//            .collection("users").document(userId)
//            .collection("steps").document(today)
//            .set(dailyStepsData)
//            .addOnSuccessListener { onSuccess() }
//            .addOnFailureListener { e -> onFailure(e) }
    }

    // 오늘 날짜의 걸음 수를 Firestore에서 가져오는 함수
    fun getTodaySteps(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("dailySteps").document(today)
            .get()
            .addOnSuccessListener { document ->
                val stepCount = document.getLong("stepCount")?.toInt() ?: 0
                onSuccess(stepCount)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
