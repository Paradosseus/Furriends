package com.innovaid.furriends.models

open class Chat(
    val sender: String? = null,
    val receiver: String? = null,
    val message: String? = null,
    val isSeen: Boolean? = false,
    val imageUrl: String? = null,
    var messageId: String? = null,
    val timestamp: String? = null,
) {
}