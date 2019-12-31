package com.example.observableexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver


class MainActivity : AppCompatActivity() {

    val TAG = "AppCompatActivity"
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun just() {
        val notesObservable = getNotesObservable()
        disposable.add(notesObservable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.setNote(it.getNote().toUpperCase())
                it
            }
            .subscribeWith(getNotesObserver()))
    }

    private fun getNotesObservable(): Observable<Note> {
        val notes = prepareNotes()
        return Observable.create { emitter ->
            if (!emitter.isDisposed) {
                for (note in notes) {
                    emitter.onNext(note)
                }
                emitter.onComplete()
            }
        }
    }

    private fun getNotesObserver(): DisposableObserver<Note> {
        return object : DisposableObserver<Note>() {

            override fun onNext(note: Note) {
                Log.d(TAG, "Note: " + note.getNote())
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "All notes are emitted!")
            }
        }
    }

    private fun prepareNotes(): List<Note> {
        val notes = arrayListOf<Note>()
        notes.add(Note(1, "buy tooth paste!"))
        notes.add(Note(2, "call brother!"))
        notes.add(Note(3, "watch narcos tonight!"))
        notes.add(Note(4, "pay power bill!"))

        return notes
    }
}
