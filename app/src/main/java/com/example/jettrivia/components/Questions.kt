package com.example.jettrivia.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screens.QuestionViewModel
import com.example.jettrivia.utils.AppColors



@Composable
fun Questions(questionsViewModel: QuestionViewModel) {
    val questions = questionsViewModel.dataOrException.value.data?.toMutableList()
    val questionIndex = remember {
        mutableStateOf<Int>(0)
    }
    //val questionsLength = questions?.size
    if (questionsViewModel.dataOrException.value.loading ==  true){
        CircularProgressIndicator()
        Log.d("Loading", "Questions:...Loading... ")
    }else{
//        Log.d("Finished", "Questions:Loading finished... ")
//        questions?.forEach { questionItem ->
//            Log.d("Result", "Questions:${questionItem.question} ")
//        }
//        Log.d("SIZE", "Questions: ${questions?.size}")
        if (questions != null){
                QuestionsDisplay(question = questions[questionIndex.value],
                    questionIndex = questionIndex, questionsViewModel){
                   questionIndex.value = questionIndex.value + 1
                }
        }
    }
}

//@Preview
@Composable
fun QuestionsDisplay(question : QuestionItem,
                     questionIndex : MutableState<Int>,
                     viewModel: QuestionViewModel,
                     onNextClicked : (Int) -> Unit = {}){
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = 10f)
    val choicesState = remember(question){
        question.choices.toMutableList()
    }

    val answerState = remember(question) {
       mutableStateOf<Int?>(null)

    }

    val correctAnswerState = remember {
        mutableStateOf<Boolean?>(null)
    }


    val questionsLength = viewModel.dataOrException.value.data?.toMutableList()?.size

    val updateAnswer : (Int) -> Unit = remember(question) {{
        answerState.value = it
        correctAnswerState.value = choicesState[it] == question.answer

    }
    }


   Surface(modifier = Modifier
       .fillMaxWidth()
       .fillMaxHeight(),
        color = AppColors.mDarkPurple) {
       Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.Top,
       horizontalAlignment = Alignment.Start) {
           if (questionIndex.value >= 3)ShowProgress(questionIndex.value)
           if (questionsLength != null) {
               QuestionTracker(counter = questionIndex.value, outOff = questionsLength)
           }
           DrawDottedLine(pathEffect)
           Column {
               Text(text = question.question, color = AppColors.mOffWhite,
               fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp,
               modifier = Modifier
                   .padding(6.dp)
                   .align(alignment = Alignment.Start)
                   .fillMaxHeight(0.3f))
               
               //choices
               choicesState.forEachIndexed { index, answerText ->
                   Row(modifier = Modifier
                       .padding(3.dp)
                       .fillMaxWidth()
                       .height(45.dp)
                       .border(
                           width = 4.dp,
                           brush = Brush.linearGradient(
                               colors =
                               listOf(
                                   AppColors.mOffWhiteDarkPurple,
                                   AppColors.mOffWhiteDarkPurple
                               )
                           ), shape = RoundedCornerShape(15.dp)
                       )
                       .clip(
                           RoundedCornerShape(
                               topStartPercent = 50, topEndPercent = 50,
                               bottomStartPercent = 50, bottomEndPercent = 50
                           )
                       )
                       .background(Color.Transparent),
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       RadioButton(selected = (answerState.value == index) , onClick = {
                        updateAnswer(index)
                       },
                       modifier = Modifier.padding(start = 16.dp),
                       colors = RadioButtonDefaults.colors(
                           selectedColor = if (correctAnswerState.value == true
                               && answerState.value == index){
                                Color.Green.copy(alpha = 0.2f)
                           }else{
                                Color.Red.copy(alpha = 0.2f)
                           }))//end of radio button

//                       Text(text = answerText)
//                       Text(text = answerState.value.toString())

                       val annotatedString = buildAnnotatedString {
                           withStyle(style = SpanStyle(fontWeight = FontWeight.Light,
                                 fontSize = 17.sp
                               , color = if (correctAnswerState.value == true
                               &&answerState.value == index){
                               Color.Green.copy(alpha = 0.2f)
                           }else if (correctAnswerState.value == false
                               &&answerState.value == index){
                               Color.Red.copy(alpha = 0.2f)
                           }else{
                               AppColors.mOffWhite
                           })){
                               append(answerText)
                           }
                       }
                       Text(annotatedString, modifier = Modifier.padding(6.dp))

                   }

               }

               Button(onClick = { onNextClicked(questionIndex.value) },
               modifier = Modifier
                   .padding(3.dp)
                   .align(Alignment.CenterHorizontally),
               shape = RoundedCornerShape(34.dp),
               colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue)
               ) {
                Text(text = "Next",
                modifier = Modifier.padding(4.dp), color = AppColors.mOffWhite, fontSize = 17.sp)
               }
               
           }

       }

    }

}
// @Preview
@Composable
fun QuestionTracker(counter : Int = 10, outOff : Int = 100){
    Text(text = buildAnnotatedString {
        withStyle(ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(SpanStyle(color = AppColors.mLightGrey, fontWeight = FontWeight.Bold
            , fontSize = 27.sp)){
                append("Question $counter /")
            }
            withStyle(SpanStyle(color = AppColors.mLightGrey, fontWeight = FontWeight.Light,
            fontSize = 14.sp)){
                append("$outOff")
            }
        }
    }, modifier = Modifier.padding(10.dp))
}


@Composable
fun DrawDottedLine(pathEffect: PathEffect){
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(5.dp)){
        drawLine(AppColors.mLightGrey, start = Offset(0f, 0f),
        end = Offset(size.width, 0f), pathEffect = pathEffect)
    }
}

@Preview
@Composable
fun ShowProgress(score : Int = 0){
    val gradient = Brush.linearGradient(colors = listOf(Color(0xfff95075),
        Color(0xffbe6be5)))

    val progressFactor = remember(score) {
        mutableStateOf(score * 0.005f)
    }
    Row(modifier = Modifier
        .padding(3.dp)
        .border(
            width = 4.dp, brush = Brush.linearGradient(
                listOf(AppColors.mLightPurple, AppColors.mLightPurple)
            ),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(
            RoundedCornerShape(
                topStartPercent = 50, topEndPercent = 50,
                bottomStartPercent = 50, bottomEndPercent = 50
            )
        )
        .fillMaxWidth()
        .height(45.dp)
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {
        Button(contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent),
            onClick = { }) {

            Text(text = (score * 10).toString(),
            modifier = Modifier.clip(RoundedCornerShape(23.dp))
                .fillMaxHeight(0.87f)
                .fillMaxWidth()
                .padding(6.dp),
            color = AppColors.mOffWhite,
            textAlign = TextAlign.Center
            )

        }
    }
}