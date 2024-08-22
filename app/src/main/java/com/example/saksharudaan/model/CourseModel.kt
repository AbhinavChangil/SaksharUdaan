package com.example.saksharudaan.model

data class CourseModel(
    var courseThumbnailUrl: String? = "",
    var courseVideoUrl: String? = "",
    var courseTitle: String? = null,
    var courseDescription: String? = null,
    var courseDuration: String? = null,
    var coursePrice: String? = null,
    var postId: String? = "",
    var postedBy: String? = "",
    var enable: String? = null
)
