package com.example.examplestep

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source


class LeaderboardViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    fun getMonthlyTotalSteps(
        month: String,
        onSuccess: (List<StepData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val stepsList = mutableListOf<StepData>()

        // 데이터가 있는지 확인
        db.collection("universities")
            .document("중앙대학교")
            .collection("totalSteps")
            .document(month)  // 예: "2024-11"
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("Firestore", "Document exists: ${documentSnapshot.data}")
                } else {
                    Log.d("Firestore", "Document does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error getting document: ${e.message}")
            }


        db.collection("universities")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("FirestoreResult", "No universities found.")
                } else {
                    querySnapshot.documents.forEach { document ->
                        val universityName = document.id

                        // 각 대학의 totalSteps 서브컬렉션에서 월별 데이터 가져오기
                        db.collection("universities")
                            .document(universityName)
                            .collection("totalSteps")
                            .document(month)  // 예: "2024-11"
                            .get()
                            .addOnSuccessListener { monthDoc ->
                                val totalSteps = monthDoc.getLong("totalSteps") ?: 0
                                stepsList.add(StepData(universityName, totalSteps.toInt()))

                                // 모든 대학의 데이터를 다 가져왔다면 정렬 후 결과 반환
                                if (stepsList.size == querySnapshot.size()) {
                                    stepsList.sortByDescending { it.totalSteps }
                                    onSuccess(stepsList)
                                }
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}


    // 월별 누적 걸음 수 데이터를 나타내는 데이터 클래스
data class StepData(
    val universityName: String,  // 대학 이름
    val totalSteps: Int // 총 걸음 수
)
