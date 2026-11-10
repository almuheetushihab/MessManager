package com.messmanager.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.theme.extraColors
import com.messmanager.app.core.util.capitalizeWords
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.feature.home.components.StatGrid
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MemberAvatar
import com.messmanager.app.ui.components.MonthPicker
import com.messmanager.app.ui.components.SectionTitle
import com.messmanager.app.ui.components.formatMoney
import kotlin.math.abs

@Composable
fun HomeScreen(
    onOpenMembers: () -> Unit,
    onNavigateToMeal: () -> Unit = {},
    onNavigateToBazar: () -> Unit = {},
    onNavigateToDeposit: () -> Unit = {},
    onNavigateToSharedExpense: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onOpenMembers = onOpenMembers,
        onNavigateToMeal = onNavigateToMeal,
        onNavigateToBazar = onNavigateToBazar,
        onNavigateToDeposit = onNavigateToDeposit,
        onNavigateToSharedExpense = onNavigateToSharedExpense,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenMembers: () -> Unit,
    onNavigateToMeal: () -> Unit,
    onNavigateToBazar: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    onNavigateToSharedExpense: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.home_title),
                actions = {
                    IconButton(onClick = onOpenMembers) {
                        Icon(
                            imageVector = Icons.Rounded.Group,
                            contentDescription = stringResource(R.string.members_title),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoading) {
            LoadingState(Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
            ) {
                MonthPicker(
                    selectedMonth = state.summary.month,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    isClosed = state.summary.isClosed,
                )

                StatGrid(state = state)

                QuickActionsRow(
                    onNavigateToMeal = onNavigateToMeal,
                    onNavigateToBazar = onNavigateToBazar,
                    onNavigateToDeposit = onNavigateToDeposit,
                    onNavigateToSharedExpense = onNavigateToSharedExpense,
                )

                NeedsAttentionSection(members = state.needsAttentionMembers)

                RecentActivitySection(
                    activities = state.recentActivities,
                    onNavigateToBazar = onNavigateToBazar,
                    onNavigateToDeposit = onNavigateToDeposit,
                )

                Text(
                    text = stringResource(R.string.home_members_count, state.summary.members.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.dimens.spaceS),
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onNavigateToMeal: () -> Unit,
    onNavigateToBazar: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    onNavigateToSharedExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
        SectionTitle(text = stringResource(R.string.home_quick_actions))
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                QuickActionButton(
                    title = stringResource(R.string.home_add_meal),
                    icon = Icons.Rounded.Restaurant,
                    onClick = onNavigateToMeal,
                    modifier = Modifier.weight(1f),
                )
                QuickActionButton(
                    title = stringResource(R.string.home_add_bazar),
                    icon = Icons.Rounded.LocalGroceryStore,
                    onClick = onNavigateToBazar,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                QuickActionButton(
                    title = stringResource(R.string.home_add_deposit),
                    icon = Icons.Rounded.AccountBalanceWallet,
                    onClick = onNavigateToDeposit,
                    modifier = Modifier.weight(1f),
                )
                QuickActionButton(
                    title = stringResource(R.string.home_shared_expense),
                    icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                    onClick = onNavigateToSharedExpense,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = MaterialTheme.dimens.spaceXs),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun NeedsAttentionSection(
    members: List<MemberSummary>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
        SectionTitle(text = stringResource(R.string.home_needs_attention))
        AnimatedVisibility(visible = members.isEmpty()) {
            AppCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.extraColors.success,
                    )
                    Text(
                        text = stringResource(R.string.home_no_due),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        AnimatedVisibility(visible = members.isNotEmpty()) {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                    members.forEachIndexed { index, summary ->
                        if (index > 0) {
                            HorizontalDivider()
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
                        ) {
                            MemberAvatar(name = summary.member.name)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = summary.member.name.capitalizeWords(),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = stringResource(R.string.home_owes, formatMoney(abs(summary.balance))),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(
    activities: List<RecentActivity>,
    onNavigateToBazar: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
        SectionTitle(text = stringResource(R.string.home_recent_activity))
        AnimatedVisibility(visible = activities.isEmpty()) {
            AppCard {
                Text(
                    text = stringResource(R.string.home_no_recent_activity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = MaterialTheme.dimens.spaceS),
                )
            }
        }
        AnimatedVisibility(visible = activities.isNotEmpty()) {
            AppCard {
                Column {
                    activities.forEachIndexed { index, activity ->
                        if (index > 0) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.spaceXs))
                        }
                        RecentActivityItem(
                            activity = activity,
                            onClick = {
                                if (activity.type == RecentActivity.ActivityType.BAZAR) {
                                    onNavigateToBazar()
                                } else {
                                    onNavigateToDeposit()
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivityItem(
    activity: RecentActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBazar = activity.type == RecentActivity.ActivityType.BAZAR
    val icon = if (isBazar) Icons.Rounded.LocalGroceryStore else Icons.Rounded.AccountBalanceWallet

    val categoryEnum = if (isBazar) BazarCategory.entries.find { it.name == activity.title } else null
    val titleText = if (categoryEnum != null) {
        stringResource(categoryEnum.labelRes)
    } else {
        activity.title.capitalizeWords()
    }

    val methodEnum = if (!isBazar) PaymentMethod.entries.find { it.name == activity.subtitle } else null
    val subtitleText = if (isBazar) {
        if (activity.subtitle.isNotBlank()) {
            stringResource(R.string.bazar_paid_by_member, activity.subtitle.capitalizeWords()) + " • " + activity.epochDay.formatEpochDay()
        } else {
            stringResource(R.string.bazar_paid_by_mess_fund) + " • " + activity.epochDay.formatEpochDay()
        }
    } else {
        if (methodEnum != null) {
            stringResource(methodEnum.labelRes) + " • " + activity.epochDay.formatEpochDay()
        } else {
            activity.subtitle + " • " + activity.epochDay.formatEpochDay()
        }
    }

    val amountColor = if (isBazar) MaterialTheme.colorScheme.primary else MaterialTheme.extraColors.success

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = MaterialTheme.dimens.spaceS)
            .defaultMinSize(minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = amountColor,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = formatMoney(activity.amount),
            style = MaterialTheme.typography.titleMedium,
            color = amountColor,
            maxLines = 1,
        )
    }
}

@ThemePreviews
@Composable
private fun HomeScreenNormalPreview() {
    PreviewSurface {
        HomeContent(
            state = PreviewData.homeNormal,
            onOpenMembers = {},
            onNavigateToMeal = {},
            onNavigateToBazar = {},
            onNavigateToDeposit = {},
            onNavigateToSharedExpense = {},
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}

@ThemePreviews
@Composable
private fun HomeScreenLoadingPreview() {
    PreviewSurface {
        HomeContent(
            state = PreviewData.homeLoading,
            onOpenMembers = {},
            onNavigateToMeal = {},
            onNavigateToBazar = {},
            onNavigateToDeposit = {},
            onNavigateToSharedExpense = {},
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}
