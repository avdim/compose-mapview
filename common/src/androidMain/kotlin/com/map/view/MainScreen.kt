package com.map.view

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.map.model.ContentState
import com.map.model.Picture
import com.map.model.toImageBitmap
import com.map.style.DarkGreen
import com.map.style.Foreground
import com.map.style.LightGray
import com.map.style.MiniatureColor
import com.map.style.Transparent
import com.map.style.icDots
import com.map.style.icRefresh

@Composable
fun MainScreen(content: ContentState) {
    Column {
        ScrollableArea(content)
    }
}

@Composable
fun TitleBar(text: String, content: ContentState) {
    TopAppBar(
        backgroundColor = DarkGreen,
        title = {
            Row(Modifier.height(50.dp)) {
                Text(
                    text,
                    color = Foreground,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                )
                Surface(
                    color = Transparent,
                    modifier = Modifier.padding(end = 20.dp).align(Alignment.CenterVertically),
                    shape = CircleShape
                ) {
                    Clickable(
                        onClick = {
                        }
                    ) {
                        Image(
                            icRefresh(),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }
        })
}


@Composable
fun Miniature(
    picture: Picture
) {
    Card(
        backgroundColor = MiniatureColor,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp).height(70.dp)
            .fillMaxWidth()
            .clickable {

            },
        shape = RectangleShape,
        elevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(end = 30.dp)) {
            Clickable(
                onClick = {

                }
            ) {
                Image(
                    picture.toImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.height(70.dp)
                        .width(90.dp)
                        .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 1.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = picture.name,
                color = Foreground,
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 16.dp),
                style = MaterialTheme.typography.body1
            )

            Image(
                icDots(),
                contentDescription = null,
                modifier = Modifier.height(70.dp)
                    .width(30.dp)
                    .padding(start = 1.dp, top = 25.dp, end = 1.dp, bottom = 25.dp),
                contentScale = ContentScale.FillHeight
            )

        }
    }
}

@Composable
fun ScrollableArea(content: ContentState) {
    val state by content.stateFlow.collectAsState()
    var index = 1
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        for (picture in state.pictures) {
            Miniature(
                picture = picture
            )
            Spacer(modifier = Modifier.height(5.dp))
            index++
        }
    }
}

@Composable
fun Divider() {
    Divider(
        color = LightGray,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    )
}
