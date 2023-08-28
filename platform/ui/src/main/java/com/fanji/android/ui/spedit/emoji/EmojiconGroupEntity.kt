package com.fanji.android.ui.spedit.emoji

import com.fanji.android.ui.spedit.emoji.DefaultGifEmoji

/**
 * 一组表情所对应的实体类
 */
class EmojiconGroupEntity(
  var icon: Int,
  var defaultGifEmojiList: List<DefaultGifEmoji>
) {
  /**
   * 组名
   */
  var name: String? = null

}
