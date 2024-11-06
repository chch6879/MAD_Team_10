package com.example.examplestep

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UniversityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // FirebaseAuth 인스턴스 초기화

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
