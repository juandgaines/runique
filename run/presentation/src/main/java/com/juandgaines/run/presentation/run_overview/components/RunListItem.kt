@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.juandgaines.run.presentation.run_overview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.juandgaines.core.domain.location.Location
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.presentation.designsystem.CalendarIcon
import com.juandgaines.core.presentation.designsystem.RunOutlinedIcon
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import com.juandgaines.run.presentation.R
import com.juandgaines.run.presentation.run_overview.mappers.toRunUi
import com.juandgaines.run.presentation.run_overview.model.RunDataUi
import com.juandgaines.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunListItem(
    modifier: Modifier = Modifier,
    runUi: RunUi,
    onDeletedClick: () -> Unit,
) {
    var showDropDown by remember { mutableStateOf(false) }
    Box {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showDropDown = true }
                )
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            MapImage(
                imageUrl = runUi.mapPictureUrl,
            )
            RunningTimeSection(
                modifier = Modifier.fillMaxWidth(),
                duration = runUi.duration
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
            RunDateSection(dateTime= runUi.dateTime)
            DataGrid(
                modifier = Modifier.fillMaxWidth(),
                run = runUi
            )
        }
        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = {
                showDropDown = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text =  stringResource(id = R.string.delete))
                },
                onClick = {
                    showDropDown = false
                    onDeletedClick()
                }
            )
        }
    }
}
@Composable
private fun MapImage(
    modifier: Modifier = Modifier,
    imageUrl: String?
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(id = R.string.run_map),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(15.dp)),
        loading = {
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(

                    text = stringResource(id = R.string.error_could_not_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}
@Composable
private fun RunDateSection(
    modifier: Modifier = Modifier,
    dateTime: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DataGrid(
    modifier: Modifier = Modifier,
    run: RunUi
) {
    val runDataUiList = listOf(
        RunDataUi(
            name = stringResource(id = R.string.distance),
            value = run.distance),
        RunDataUi(
            name = stringResource(id = R.string.avg_speed),
            value = run.avgSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.max_speed),
            value = run.maxSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.pace),
            value = run.pace
        ),
        RunDataUi(
            name = stringResource(id = R.string.total_elevation),
            value = run.totalElevation
        )
    )
    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current){
        maxWidth.toDp()
    }
    FlowRow (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        runDataUiList.forEach { run ->
            DataGridCell(
                runData = run,
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = maxWidthDp
                    )
                    .onSizeChanged {
                        maxWidth = max(maxWidth, it.width)
                    }
            )
        }
    }
}
@Composable
private fun DataGridCell(
    modifier : Modifier = Modifier,
    runData: RunDataUi,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = runData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun RunningTimeSection(
    modifier: Modifier = Modifier,
    duration:String
){
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier =   Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id =  R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview
@Composable
fun RunListItemPreview() {
    RuniqueTheme {
        RunListItem(
            runUi = Run(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 2543,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 15.6234,
                totalElevationMeters = 123,
                mapPictureUrl = null
            ).toRunUi(),
            onDeletedClick = {}
        )
    }

}