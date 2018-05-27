package com.example.ruslan.curs2project.ui.fragments.lists.vid.add_point;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.api.ApiFactory;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.model.pojo.Message;
import com.example.ruslan.curs2project.model.pojo.Notification;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.utils.FormatterUtil;

import java.util.Date;

import io.reactivex.Single;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

@InjectViewState
public class AddPointPresenter extends MvpPresenter<AddPointView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d(TAG_LOG,"attach presenter");
    }

    public void createPoint(BookCrossing crossing, Point point) {
        RepositoryProvider.getPointRepository()
                .createPoint(crossing,point);
    }

    public boolean validateKey(BookCrossing crossing, String key) {
        String crossingKey = crossing.getKeyPhrase();
        if(key.equals(crossingKey)) {
            return true;
        }
        return false;
    }

    public void sendMessage(BookCrossing crossing) {
        Message message = new Message();
        Notification notification = new Notification();
        notification.setTitle("In " + crossing.getName() + " changed date");
        notification.setBody("new data : " + FormatterUtil.formatFirebaseDay(new Date(crossing.getDate())) +
                " and owner : " + UserRepository.getCurrentId());

        message.setNotification(notification);
        message.setTo("/topics/" + crossing.getId());

        Single<String> response = ApiFactory.getMessageService()
                .sendMessage(message);

        Log.d(TAG_LOG,"resp = " + response.subscribe(s -> s.toString()));
    }
}
