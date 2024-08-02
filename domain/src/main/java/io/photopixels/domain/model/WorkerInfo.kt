package io.photopixels.domain.model

data class WorkerInfo(
    val workerTag: String,
    val workerStatus: WorkerStatus,
    val resultData: Map<String, Any>? = null
) {
    companion object {
        const val UPLOAD_PHOTOS_WORKER_RESULT_KEY = "uploaded_photos"
        const val WORKER_ERROR_RESULT_KEY = "worker_error"
    }
}

enum class WorkerStatus {
    FINISHED,
    FAILED
}
