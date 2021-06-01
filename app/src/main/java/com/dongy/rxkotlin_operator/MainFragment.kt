package com.dongy.rxkotlin_operator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observables.ConnectableObservable
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private const val TAG: String = "dongy"
    }

    private var disposeBag = CompositeDisposable() //CompositeDisposable.add(Disposable)

    private lateinit var viewModel: MainViewModel

    private lateinit var resultObservable: Observable<String>
    private lateinit var mObservableEmitter: ObservableEmitter<String>

    // Connectable observable
    private lateinit var connectableObservable: ConnectableObservable<Long>
    private lateinit var connectedObservable: Disposable

    private val countValue: String
        get() = countEditText.text.toString()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false).apply {
            setupObservable()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.dispose()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        bind()
    }

    private fun setupObservable() {
        resultObservable = Observable.create<String> { observableEmitter ->
            mObservableEmitter = observableEmitter
        }
        //위 emitter 연결은 아래 코드와 같다
//        resultObservable = Observable.create<String> (object : ObservableOnSubscribe<String>{
//            override fun subscribe(emitter: ObservableEmitter<String>) {
//                mObservableEmitter = emitter
//            }
//        })

        //Observable 에 publish 연산자를 붙여 만든다.
        //subscribe 호출로 배출을 시작하지만 connect 메서드를 호출한 후에 활성화 된다.
        //Publish Subject 과 사용방법은 다르지만 같은 동작을 한다.
        connectableObservable = Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .publish()
    }


    private fun bind() {

        resultObservable
                .doOnNext {
                    Log.d(TAG, "Emit: $it")
                }
                .subscribe {
                    observableResultTextView.text = it
                }
                .disposed(by = disposeBag)

        viewModel.countBehaviorSubject
                .subscribe {
                    mObservableEmitter.onNext(it)
                }
                .disposed(by = disposeBag)

        viewModel.countBehaviorRelay
                .subscribe {
                    mObservableEmitter.onNext(it)
                }
                .disposed(by = disposeBag)

        observableJustTriggerButton
                .clicks()
                .subscribe {
                    observableJust()
                }
                .disposed(by = disposeBag)

        observableRangeTriggerButton
                .clicks()
                .subscribe {
                    observableRange()
                }
                .disposed(by = disposeBag)

        observableRepeatTriggerButton
                .clicks()
                .subscribe {
                    observableRepeat()
                }
                .disposed(by = disposeBag)

        observableFromTriggerButton
                .clicks()
                .subscribe {
                    observableFrom()
                }
                .disposed(by = disposeBag)

        observableStartTriggerButton
                .clicks()
                .subscribe {
                    observableStart()
                }
                .disposed(by = disposeBag)

        observableIntervalTriggerButton
                .clicks()
                .subscribe {
                    observableInterval()
                }
                .disposed(by = disposeBag)

        observableSubscribeTriggerButton
                .clicks()
                .subscribe {
                    subscribeConnectableObservable()
                }
                .disposed(by = disposeBag)

        observableConnectTriggerButton
                .clicks()
                .subscribe {
                    observableConnect()
                }
                .disposed(by = disposeBag)

        observableDisconnectTriggerButton
                .clicks()
                .subscribe {
                    observableDisconnect()
                }
                .disposed(by = disposeBag)

        singleCreateTriggerButton
                .clicks()
                .subscribe {
                    singleCreate()
                }
                .disposed(by = disposeBag)

        behaviorSubjectTriggerButton
                .clicks()
                .subscribe {
                    viewModel.tapBehaviorSubjectButton()
                }
                .disposed(by = disposeBag)

        behaviorRelayTriggerButton
                .clicks()
                .subscribe {
                    viewModel.tapBehaviorRelayButton()
                }
                .disposed(by = disposeBag)

        clearButton
                .clicks()
                .subscribe {
                    mObservableEmitter.onNext("")
                }
                .disposed(by = disposeBag)

        disposeTriggerButton
                .clicks()
                .throttleFirst(1000, TimeUnit.MILLISECONDS) // 1초에 한번 첫번째 클릭만 통과
                .subscribe {
                    Log.d(TAG, "Disposed onNext: $it")
                    disposeResultTextView.text = "Disposed"
                    disposeBag.dispose()
                }
                .disposed(by = disposeBag)

    }

    private fun observableJust() {
        Observable
                .just(countValue)
                .subscribe {
                    mObservableEmitter.onNext(it)
                }
                .disposed(disposeBag)

    }

    private fun observableRange() {
        Observable
                .range(0, countValue.toInt())
                .subscribe {
                    val result = "${observableResultTextView.text} $it"
                    mObservableEmitter.onNext(result)
                }
                .disposed(disposeBag)
    }

    private fun observableRepeat() {
        Observable
                .just(countValue)
                .repeat()
                .take(10) //take 없으면 영원히 반복함
                .subscribe {
                    Log.d(TAG, "observableRepeat: $it")
                    val result = "${observableResultTextView.text} $it"
                    mObservableEmitter.onNext(result)
                }
                .disposed(by = disposeBag)
    }

    private fun observableFrom() {
        // fromArray
        val arr = Array(10) { i -> i * countValue.toInt() }
        Observable.fromArray(arr) //fromIterable 도 있다.
                .subscribe {
                    Log.d(TAG, "observableFrom: size - ${it.size}")
                    for (item in it) {
                        val result = "${observableResultTextView.text} $item"
                        mObservableEmitter.onNext(result)
                    }
                }
                .disposed(by = disposeBag)
    }

    private fun observableStart() {
        // from Callable
        val callable = Callable<String> {
            Thread.sleep(3000)
            "Hello"
        }

        Observable.fromCallable(callable)
                .subscribe {
                    Log.d(TAG, "observableFrom: $it")
                    mObservableEmitter.onNext(it)
                }
                .disposed(by = disposeBag)
    }

    private fun observableInterval() {
        Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .subscribe {
                    Log.d(TAG, "observableInterval: $it")
                    mObservableEmitter.onNext(it.toString())
                }
                .disposed(by = disposeBag)
    }

    private fun subscribeConnectableObservable() {
        connectableObservable
                .subscribe {
                    Log.d(TAG, "connectable Observable result $it")
                    /**/observableResultTextView.text = "" //clear
                    mObservableEmitter.onNext(it.toString())
                }
                .disposed(by = disposeBag)
    }

    private fun observableConnect() {
        connectedObservable = connectableObservable.connect() //subscribe 보다 connect 먼저 누르면 데이터가 먼저 흐르고 있음...
    }

    private fun observableDisconnect() {
        if (!::connectedObservable.isInitialized) {
            Log.d(TAG, "observableConnect: not initialized")
            return
        }
        connectedObservable.dispose()
    }

    private fun singleCreate() {
        Single.create<String> {
            // Do something..
            // 퍼미션 받고 콜백핸들러로 값을 받아옴
            // API 콜을 통해서 repsonse값을 받아온다던가..!?
            when (countValue.toInt()) {
                200 -> {
                    it.onSuccess("dongy:: Matched!")// case 1: disposed
                }
                400 -> {
                    it.onError(Throwable("dongy:: Code: 400"))
                }
                else -> {
                    throw RuntimeException("dongy:: Unknown response") // case 2: throw
                }
            }
        }.subscribe({
            // Success
            Log.d(TAG, "dongy:: singleCreate: Success - $it")
        }, {
            // Error
            Log.d(TAG, "dongy:: sin gleCreate: Error!! $it")
        })
    }

}

fun Disposable.disposed(by: CompositeDisposable): Disposable {
    by.add(this)
    return this
}
