package main.se.miun.dt176g.anby2001.reactive.multiplayer;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import main.se.miun.dt176g.anby2001.reactive.draw.Drawing;
import main.se.miun.dt176g.anby2001.reactive.draw.DrawingPanel;
import main.se.miun.dt176g.anby2001.reactive.shapes.Shape;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Client side
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Client {
    private final DrawingPanel drawingPanel;
    public Client(DrawingPanel drawingPanel) throws Exception {
        Socket socket = new Socket("localhost", 12345);
        this.drawingPanel = drawingPanel;

        final CountDownLatch latch = new CountDownLatch(1);
        List<String> exitCommands = List.of("disconnect", "exit", "quit");
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        // Sends shape to server
        drawingPanel.shapeObservable()
                .subscribeOn(Schedulers.io())
                .takeWhile(s -> !exitCommands.contains(s))
                .doFinally(latch::countDown)
                .subscribe(outputStream::writeObject, err -> System.err.println(err.getMessage()));

        // Receive Drawing panel
        Observable.just(socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new)
                .subscribe(drawing -> {
                    this.drawingPanel.setDrawing((Drawing) drawing.readObject());

                });

        // Receive shapes from server
        Disposable networkDisposable = Observable.<Shape>create(emitter -> {
                    Observable.just(socket)
                            .map(Socket::getInputStream)
                            .map(ObjectInputStream::new)
                            .subscribe(shape -> {
                                while(!emitter.isDisposed()) {
                                    emitter.onNext((Shape) shape.readObject());
                                }
                            }, err -> System.err.println(err.getMessage()));
                })
                .subscribeOn(Schedulers.io())
                .subscribe(this.drawingPanel::addShape
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));


        latch.await();

        networkDisposable.dispose();
        Schedulers.shutdown();

        System.out.println("Exiting");
    }

}
