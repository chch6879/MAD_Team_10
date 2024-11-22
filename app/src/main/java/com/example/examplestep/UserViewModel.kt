    package com.example.examplestep

    import android.app.Activity
    import android.content.Context
    import android.content.pm.PackageManager
    import android.hardware.Sensor
    import android.hardware.SensorEvent
    import android.hardware.SensorEventListener
    import android.hardware.SensorManager
    import android.os.Build
    import android.util.Log
    import androidx.compose.runtime.State
    import androidx.compose.runtime.mutableStateOf
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.DocumentReference
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.*

    class UserViewModel : ViewModel() {
        private val db = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()
        var universityName: String? = null
            private set

        private val _stepCount = MutableStateFlow(0) // 걸음 수를 상태로 관리
        val stepCount: StateFlow<Int> get() = _stepCount

        // 키와 몸무게를 상태로 관리
        private val _height = MutableStateFlow(0)
        val height: StateFlow<Int> get() = _height

        private val _weight = MutableStateFlow(0)
        val weight: StateFlow<Int> get() = _weight



        private val today: String
            get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        private val currentMonth: String
            get() = today.substring(0, 7) // "yyyy-MM" 형식으로 현재 월 추출

        private var lastUpdateDate: String? = null // 마지막 업데이트된 날짜

        private var previousStepCount: Int = 0 // 이전 걸음 수 저장 변수


        private val _loading = mutableStateOf(true)
        val loading: State<Boolean> get() = _loading

        // 날짜 변경을 확인하고, 필요 시 초기화하는 함수
        private fun checkDateChange() {
            if (lastUpdateDate != today) {
                // 날짜가 변경되었으면 previousStepCount를 초기화
                previousStepCount = 0
                lastUpdateDate = today
            }
        }

        init {
            loadUserUniversity()

            getTodayStepsAndUserData(
                onSuccess = { height, weight, stepCount ->
                    previousStepCount = stepCount
                    _height.value = height
                    _weight.value = weight
                    _stepCount.value = stepCount // 초기 걸음수 설정
                    lastUpdateDate = today // 초기화 시점에서 날짜 저장
                    _loading.value = false // 데이터 로딩 완료
                },
                onFailure = { e ->
                    e.printStackTrace()
                }
            )
        }

        fun updateStepCount(newSteps: Int) {
            viewModelScope.launch {
                val totalSteps = previousStepCount + newSteps
                _stepCount.value = totalSteps
                updateDailySteps(
                    totalSteps,
                    onSuccess = { Log.d("UserViewModel", "Steps updated successfully") },
                    onFailure = { e -> Log.e("UserViewModel", "Failed to update steps", e) }
                )
            }
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

        fun updateDailySteps(stepCount: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            val userId = auth.currentUser?.uid ?: return
            val universityName = universityName ?: return // 대학명을 로드한 후 사용
            val dailyStepsData = hashMapOf("stepCount" to stepCount)

            Log.d("UpdateDailySteps", "Starting updateDailySteps with stepCount: $stepCount")
            Log.d("UpdateDailySteps", "UserId: $userId, UniversityName: $universityName")

            // 날짜가 바뀌면 파이버베이스 users컬렉션의 previousStep을 초기화 해야됨
            checkDateChange() // 매번 호출 시 날짜 확인

            // 먼저 이전 걸음 수를 가져와서 차이를 계산
            getPreviousStepCount(
                onSuccess = { previousStepCount ->
                    val stepDifference = stepCount - previousStepCount
                    if (stepDifference <= 0) return@getPreviousStepCount // 증가하지 않은 경우 업데이트 생략

                    Log.d("UpdateDailySteps InININ", "PreviousStepCount: $previousStepCount")

                    // 대학 및 사용자 문서에 대해 업데이트
                    val dailyStepsRefUniversityCollection = db.collection("universities")
                        .document(universityName)
                        .collection("users")
                        .document(userId)
                        .collection("dailySteps")
                        .document(today) // 날짜를 문서 ID로 사용

                    val dailyStepsRefUserCollection = db.collection("users")
                        .document(userId)
                        .collection("dailySteps")
                        .document(today)


                    Log.d("dailyStepsRefUniversityCollection", "dailyStepsRefUniversityCollection: $dailyStepsRefUniversityCollection")
                    Log.d("dailyStepsRefUserCollection InININ", "dailyStepsRefUserCollection: $dailyStepsRefUserCollection")
                    // 공통 함수로 중복 제거: Firestore에 데이터 업데이트 또는 설정
                    fun updateFirestoreDocument(docRef: DocumentReference) {
                        docRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    docRef.update(dailyStepsData as Map<String, Any>)
                                        .addOnSuccessListener { onSuccess() }
                                        .addOnFailureListener(onFailure)
                                } else {
                                    docRef.set(dailyStepsData as Map<String, Any>)
                                        .addOnSuccessListener { onSuccess() }
                                        .addOnFailureListener(onFailure)
                                }
                            }
                            .addOnFailureListener(onFailure)
                    }

                    // 대학 및 사용자 문서에 대해 업데이트
                    updateFirestoreDocument(dailyStepsRefUniversityCollection)
                    updateFirestoreDocument(dailyStepsRefUserCollection)

                    // 월별 누적 걸음 수 업데이트
                    updateMonthlyTotalSteps(stepDifference, onSuccess, onFailure)

                    // previousStepCount를 Firestore에 저장하여 다음에 앱이 초기화될 때 불러올 수 있도록 설정
                    savePreviousStepCount(stepCount, onSuccess, onFailure)


                },
                onFailure = { e ->
                    e.printStackTrace()
                    onFailure(e) // 실패시 콜백 처리
                }
            )
        }


        fun setupSensor(context: Context, activity: Activity) {
            // 권한 체크
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10(Q) 이상에서는 `ACTIVITY_RECOGNITION` 권한 필요
                if (ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.ACTIVITY_RECOGNITION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 없다면, 권한 요청
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                        1001 // 권한 요청 코드
                    )
                } else {
                    // 권한이 이미 있다면, 센서 설정
                    registerStepSensor(context)
                }
            } else {
                // Android 10 이하에서는 별도의 권한 요청이 필요 없으므로 바로 센서 설정
                registerStepSensor(context)
            }
        }

        private fun registerStepSensor(context: Context) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            if (stepDetectorSensor == null) {
                Log.e("StepSensor", "Step detector sensor is not available on this device")
                return
            }
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                        val newStepCount = _stepCount.value + event.values[0].toInt()
                        _stepCount.value = newStepCount
                        setStepCount(newStepCount)
                        updateDailySteps(
                            newStepCount,
                            onSuccess = {Log.d("StepSensor", "Steps successfully updated to Firestore")},
                            onFailure = { e -> Log.e("StepSensor", "Failed to update steps", e) }
                        )
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(
                sensorEventListener,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }



        private fun savePreviousStepCount(stepCount: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            val userId = auth.currentUser?.uid ?: return
            db.collection("users")
                .document(userId)
                .update("previousStepCount", stepCount)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }
        }

//        private fun getPreviousStepCount(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
//            val userId = auth.currentUser?.uid ?: return
//            db.collection("users")
//                .document(userId)
//                .get()
//                .addOnSuccessListener { document ->
//                    val stepCount = document.getLong("previousStepCount")?.toInt() ?: 0
//                    onSuccess(stepCount)
//                }
//                .addOnFailureListener(onFailure)
//        }

        fun getPreviousStepCount(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
            val userId = auth.currentUser?.uid ?: return
            val dailyStepsRef = db.collection("users").document(userId).collection("dailySteps").document(today)

            dailyStepsRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val previousStepCount = documentSnapshot.getLong("stepCount")?.toInt() ?: 0
                        Log.d("GetPreviousStepCount", "Previous step count: $previousStepCount")
                        onSuccess(previousStepCount)
                    } else {
                        Log.d("GetPreviousStepCount", "No document found, returning 0")
                        onSuccess(0) // 문서가 없을 경우 0으로 처리
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("GetPreviousStepCount", "Failed to get previous step count", e)
                    onFailure(e)
                }
        }

        // 월별 누적 걸음 수를 Firestore에 업데이트하는 함수
        private fun updateMonthlyTotalSteps(stepCount: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            val universityName = universityName ?: return
            val stepData = hashMapOf("totalSteps" to stepCount)



            // 해당 대학의 totalSteps 컬렉션에서 월별 누적 걸음 수 문서 가져오기
            val monthlyTotalStepsRef = db.collection("universities")
                .document(universityName)
                .collection("totalSteps")
                .document(currentMonth)

            monthlyTotalStepsRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // 기존 걸음 수를 가져와 누적
                        val currentTotal = documentSnapshot.getLong("totalSteps") ?: 0
                        monthlyTotalStepsRef.update("totalSteps", currentTotal + stepCount)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(e) }
                    } else {
                        // 문서가 없으면 새로 생성하여 초기화
                        monthlyTotalStepsRef.set(stepData)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(e) }
                    }
                }
                .addOnFailureListener { e -> onFailure(e) }



        }

        fun getTodayStepsAndUserData(
            onSuccess: (Int, Int, Int) -> Unit,  // 걸음 수, 키, 몸무게
            onFailure: (Exception) -> Unit
        ) {
            val userId = auth.currentUser?.uid ?: return
            val universityName = universityName ?: return // 대학명 확인

            // 사용자 문서에서 키와 몸무게 가져오기
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument.exists()) {
                        // 키와 몸무게 데이터 가져오기
                        val height = userDocument.getLong("height")?.toInt() ?: 0
                        val weight = userDocument.getLong("weight")?.toInt() ?: 0

                        // dailySteps 하위 문서에서 걸음 수 가져오기
                        db.collection("users").document(userId)
                            .collection("dailySteps").document(today)
                            .get()
                            .addOnSuccessListener { stepDocument ->
                                if (stepDocument.exists()) {
                                    val stepCount = stepDocument.getLong("stepCount")?.toInt() ?: 0
                                    onSuccess(stepCount, height, weight)
                                } else {
                                    // 걸음 수가 없으면 초기화 후 반환
                                    val initialStepCount = 0
                                    val dailyStepsData = hashMapOf("stepCount" to initialStepCount)

                                    // 사용자 문서에 걸음 수 초기화
                                    db.collection("users").document(userId)
                                        .collection("dailySteps").document(today)
                                        .set(dailyStepsData)
                                        .addOnSuccessListener {
                                            onSuccess(initialStepCount, height, weight)
                                        }
                                        .addOnFailureListener { e ->
                                            onFailure(e)
                                        }

                                    // 대학 문서에 걸음 수 초기화
                                    db.collection("universities").document(universityName)
                                        .collection("users").document(userId)
                                        .collection("dailySteps").document(today)
                                        .set(dailyStepsData)
                                        .addOnFailureListener { e ->
                                            onFailure(e)
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    } else {
                        onFailure(Exception("User document not found"))
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }


        // 걸음 수 설정 (초기화 또는 업데이트)
        fun setStepCount(count: Int) {
            _stepCount.value = count
        }

        fun setHeightAndWeight(height: Int, weight: Int) {
            _height.value = height
            _weight.value = weight
        }

        // 사용자의 키와 몸무게 입력
        fun saveUserHeightAndWeight(
            height: Int,
            weight: Int,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val userId = auth.currentUser?.uid ?: return
            val userRef = db.collection("users").document(userId)

            val data = mapOf(
                "height" to height,
                "weight" to weight
            )

            userRef.update(data)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e) }

            setHeightAndWeight(height,weight)
        }


    }
