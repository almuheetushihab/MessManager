package com.messmanager.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimens.spaceL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = message,
            modifier = Modifier.size(MaterialTheme.dimens.iconXl),
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = MaterialTheme.dimens.spaceM),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onActionClick != null) {
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .padding(top = MaterialTheme.dimens.spaceL)
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleItem(
    isDismissible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (isDismissible) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                    onDismiss()
                    true
                } else {
                    false
                }
            },
        )
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
            modifier = modifier,
            backgroundContent = {
                val color by animateColorAsState(
                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        Color.Transparent
                    },
                    label = "dismissBackgroundColor",
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = MaterialTheme.dimens.spaceM),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.content_desc_delete),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            },
        ) {
            content()
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingStatePreview() {
    PreviewSurface {
        LoadingState()
    }
}

@ThemePreviews
@Composable
private fun EmptyStatePreview() {
    PreviewSurface {
        EmptyState(
            message = stringResource(R.string.sample_no_items),
            icon = Icons.Rounded.LocalGroceryStore,
        )
    }
}
