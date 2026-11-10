package com.messmanager.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.capitalizeWords
import kotlin.math.abs

@Composable
fun MemberAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val capitalized = name.capitalizeWords().trim()
    val initial = capitalized.firstOrNull()?.uppercase() ?: "?"

    val colorIndex = abs(name.hashCode()) % avatarColorResIds.size
    val backgroundColor = colorResource(avatarColorResIds[colorIndex])

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .semantics {
                contentDescription = name
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.45).sp,
            ),
            color = colorResource(R.color.on_primary),
        )
    }
}

private val avatarColorResIds = listOf(
    R.color.avatar_1,
    R.color.avatar_2,
    R.color.avatar_3,
    R.color.avatar_4,
    R.color.avatar_5,
    R.color.avatar_6,
    R.color.avatar_7,
    R.color.avatar_8,
)

@ThemePreviews
@Composable
private fun MemberAvatarPreview() {
    PreviewSurface {
        Row(
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            MemberAvatar(name = "Rahim")
            MemberAvatar(name = "Karim")
            MemberAvatar(name = "Tanvir")
            MemberAvatar(name = "Shakib")
        }
    }
}
