package com.example.examplestep

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UniversityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // FirebaseAuth 인스턴스 초기화

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val currentMonth: String
        get() = today.substring(0, 7) // "yyyy-MM" 형식으로 현재 월 추출

    fun saveUniversityNameToFirebase(universityName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = getCurrentUserId() // 현재 사용자 ID를 가져오는 메서드 필요
        val userDisplayName = getCurrentUserDisplayName() // 현재 사용자 ID를 가져오는 메서드 필요

        // Firestore에 저장할 데이터
        val userData = hashMapOf(
            "userId" to userId,
            "userName" to userDisplayName
        )

        // 대학 이름을 문서 ID로 사용하고, 해당 문서의 서브컬렉션에 사용자 정보를 저장
        db.collection("universities").document(universityName)
            .collection("users") // 서브컬렉션 생성
            .document(userId) // 각 사용자의 ID로 문서 생성
            .set(userData)
            .addOnSuccessListener {
                // 성공적으로 저장된 후 실행할 코드
                onSuccess()

                // Add dailySteps collection for the user in the university's user document.
                addDailyStepsCollection(userId, universityName)
                addTotalStepsCollection(universityName)
                addIsExistDocument(universityName)
            }
            .addOnFailureListener { e ->
                // 저장 실패 시 실행할 코드
                onFailure(e)
            }

        // 사용자 컬렉션에 추가
        db.collection("users").document(userId)
            .set(hashMapOf(
                "userId" to userId,
                "userName" to userDisplayName,
                "university" to universityName
            ))
            .addOnFailureListener { e ->
                // 사용자 정보 저장 실패 시 처리
                onFailure(e)
            }
    }

    // 최초 회원가입을 통해 정보를 입력받으면 각 데이터 table에 자신의 오늘 날짜에 대한 stepCount를 0으로 초기화
    private fun addDailyStepsCollection(userId: String, universityName: String) {
        // Ensure dailySteps collection exists for the user in universities/{universityName}/users/{userId}/
        val dailyStepsData = hashMapOf("stepCount" to 0)

        // Creating dailySteps for the user under the university's collection
       val userDailyStepsDocRefUniversityCollection = db.collection("universities")
            .document(universityName)
            .collection("users")
            .document(userId)
            .collection("dailySteps") // Create dailySteps subcollection for this user
            .document(today) // Assuming "today" as the document for daily steps

        userDailyStepsDocRefUniversityCollection.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Document doesn't exist, create it
                    val dailyStepsData = hashMapOf("stepCount" to 0)
                    userDailyStepsDocRefUniversityCollection.set(dailyStepsData)
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

        val userDailyStepsDocRefUsersCollection = db.collection("users")
            .document(userId)
            .collection("dailySteps") // Create dailySteps subcollection for this user
            .document(today) // Assuming "today" as the document for daily steps

        userDailyStepsDocRefUsersCollection.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Document doesn't exist, create it
                    val dailyStepsData2 = hashMapOf("stepCount" to 0)
                    userDailyStepsDocRefUsersCollection.set(dailyStepsData2)
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    
    // totalStep 컬렉션 생성
    private fun addTotalStepsCollection(universityName: String) {
        // Ensure totalSteps collection exists at the university level
        val totalStepsDocRef = db.collection("universities")
            .document(universityName)
            .collection("totalSteps")
            .document(currentMonth) // Example: Creating a document for this month's total steps


        // 트랜잭션을 사용하여 데이터를 안전하게 업데이트
        db.runTransaction { transaction ->
            val snapshot = transaction.get(totalStepsDocRef)

            // 문서가 존재하지 않으면 새로 생성
            if (!snapshot.exists()) {
                val totalStepsData = hashMapOf("totalSteps" to 0)
                transaction.set(totalStepsDocRef, totalStepsData)
            } else {
                // 문서가 존재하면 업데이트
                val existingTotalSteps = snapshot.getLong("totalSteps") ?: 0
                transaction.update(totalStepsDocRef, "totalSteps", existingTotalSteps)
            }
        }
            .addOnSuccessListener {
                // 트랜잭션 성공
                println("Total steps document successfully updated!")
            }
            .addOnFailureListener { e ->
                // 트랜잭션 실패
                e.printStackTrace()
            }
    }

    private fun addIsExistDocument(universityName: String){
        // universities -> {universityName} 경로에 isExist 필드를 추가
        val universityDocRef = db.collection("universities").document(universityName)

        db.runTransaction { transaction ->
            // 기존 university 문서 가져오기
            val universitySnapshot = transaction.get(universityDocRef)

            // 문서가 존재하지 않으면 새로 생성하고 필드 추가
            if (!universitySnapshot.exists()) {
                // university 문서 생성 및 isExist 필드 추가
                val data = hashMapOf("isExist" to true)
                transaction.set(universityDocRef, data)
            } else {
                // 문서가 이미 존재하는 경우 isExist 필드를 업데이트
                transaction.update(universityDocRef, "isExist", true)
            }
        }
            .addOnSuccessListener {
                println("isExist field successfully added to the university document!")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // 현재 사용자 ID를 가져오는 메서드 구현 필요
    private fun getCurrentUserId(): String {
        // 현재 사용자 ID를 반환하는 로직 구현
        return auth.currentUser?.uid   ?: "" // 현재 사용자 ID를 반환하거나, 없으면 빈 문자열 반환
    }

    // 현재 사용자 이름를 가져오는 메서드 구현 필요
    private fun getCurrentUserDisplayName (): String {
        // 현재 사용자 ID를 반환하는 로직 구현
        return auth.currentUser?.displayName    ?: "" // 현재 사용자 ID를 반환하거나, 없으면 빈 문자열 반환
    }
}
