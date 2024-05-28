package com.scalefocus.domain.model

data class WorkerInfo(val workerTag: String, val workerStatus: WorkerStatus, val resultData: Map<String, Any>? = null) {
    companion object {
        const val UPLOAD_PHOTOS_WORKER_RESULT_KEY = "uploaded_photos"
    }
}

enum class WorkerStatus {
    FINISHED
}
