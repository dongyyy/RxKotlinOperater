package com.dongy.rxkotlin_operator

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel : ViewModel() {

    val countBehaviorSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("0")
    val countBehaviorRelay: BehaviorRelay<String> = BehaviorRelay.createDefault("0")

    fun tapBehaviorSubjectButton() {
        val result = countBehaviorSubject.value.toInt() + 10
        countBehaviorSubject.onNext(result.toString())

        if (result > 50) {
            countBehaviorSubject.onComplete()
        }
    }

    fun tapBehaviorRelayButton() {
        val result = countBehaviorRelay.value.toInt() + 10
        countBehaviorRelay.accept(result.toString()) // onNext와 비슷

        if (result > 50) {
            countBehaviorRelay.onErrorComplete()
            //Relay 는 onComplete 이후 onNext 써도 애러가 안남
            //Observer 의 onComplete 콜백도 호출 안함
            //UI와 연관되서 많이 사용
        }
    }
}