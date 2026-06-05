package com.saudepilates.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saudepilates.app.ui.theme.IosColors
import com.saudepilates.app.util.ToastManager
import com.saudepilates.app.util.ToastType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IosScreen(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = IosColors.groupedBackground,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IosColors.groupedBackground,
                    titleContentColor = IosColors.label
                )
            )
        },
        content = content
    )
}

@Composable
fun IosGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
        color = IosColors.secondaryGroupedBackground
    ) {
        Column(content = content)
    }
}

@Composable
fun IosSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title.uppercase(),
        modifier = modifier.padding(start = 32.dp, top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelMedium,
        color = IosColors.secondaryLabel,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun IosNavigationRow(
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean = true,
    destructive: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (destructive) IosColors.red else IosColors.label
            )
            if (!destructive) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IosColors.secondaryLabel)
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = IosColors.separator.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun IosHubList(
    title: String,
    items: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null
) {
    IosScreen(title = title, modifier = modifier) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                IosGroup {
                    items.forEachIndexed { index, (label, action) ->
                        IosNavigationRow(title = label, onClick = action, showDivider = index < items.lastIndex)
                    }
                }
            }
            footer?.let {
                item { Spacer(Modifier.height(24.dp)); it() }
            }
        }
    }
}

@Composable
fun IosStatCard(title: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.10f))
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = IosColors.secondaryLabel)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IosColors.label)
    }
}

@Composable
fun IosQuickActionRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(IosColors.secondaryGroupedBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = IosColors.blue)
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IosColors.secondaryLabel)
    }
}

@Composable
fun IosListRow(
    title: String,
    subtitle: String? = null,
    tertiary: String? = null,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                subtitle?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = IosColors.secondaryLabel)
                }
                tertiary?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = IosColors.secondaryLabel)
                }
            }
            trailing?.invoke()
        }
        if (showDivider) {
            HorizontalDivider(Modifier.padding(start = 16.dp), color = IosColors.separator.copy(alpha = 0.35f))
        }
    }
}

@Composable
fun IosLabeledRow(label: String, value: String, showDivider: Boolean = true) {
    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = IosColors.secondaryLabel)
        }
        if (showDivider) HorizontalDivider(Modifier.padding(start = 16.dp), color = IosColors.separator.copy(alpha = 0.35f))
    }
}

@Composable
fun IosBadge(text: String, color: Color = IosColors.blue) {
    Text(
        text,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IosSegmented(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun IosEmptyState(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = IosColors.secondaryLabel)
    }
}

@Composable
fun IosLoadingOverlay(isLoading: Boolean) {
    if (isLoading) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = IosColors.blue)
        }
    }
}

@Composable
fun IosFormScroll(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
fun IosDropdownField(
    label: String,
    options: List<String>,
    values: List<String>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val index = values.indexOf(selectedValue)
    val display = if (index >= 0) options[index] else "Selecione"
    Box(modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        IosListRow("$label: $display", showDivider = false, onClick = { expanded = true }, trailing = {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IosColors.secondaryLabel)
        })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEachIndexed { i, value ->
                DropdownMenuItem(text = { Text(options[i]) }, onClick = { onSelect(value); expanded = false })
            }
        }
    }
}

@Composable
fun ToastBannerHost(modifier: Modifier = Modifier) {
    val toast by ToastManager.toast.collectAsState()
    AnimatedVisibility(
        visible = toast != null,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        toast?.let { current ->
            val (foreground, background, icon) = when (current.type) {
                ToastType.SUCCESS -> Triple(IosColors.green, IosColors.green.copy(0.12f), Icons.Default.CheckCircle)
                ToastType.ERROR -> Triple(IosColors.red, IosColors.red.copy(0.12f), Icons.Default.Error)
                ToastType.WARNING -> Triple(IosColors.orange, IosColors.orange.copy(0.12f), Icons.Default.Warning)
                ToastType.INFO -> Triple(IosColors.blue, IosColors.blue.copy(0.12f), Icons.Default.Info)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = foreground)
                Text(current.message, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = foreground)
            }
        }
    }
}
