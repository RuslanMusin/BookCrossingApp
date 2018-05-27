package com.example.ruslan.curs2project.repository.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.api.ApiFactory;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.pojo.GBook;
import com.example.ruslan.curs2project.model.pojo.ImageLinks;
import com.example.ruslan.curs2project.model.pojo.SearchResult;
import com.example.ruslan.curs2project.model.pojo.VolumeInfo;
import com.example.ruslan.curs2project.utils.RxUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;

import static android.support.constraint.Constraints.TAG;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_MAX_RESULTS;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_ORDER_BOOK;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_PROJECTION;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_QUERY;

public class BookApiRepositoryImpl implements BookApiRepository {

    @NonNull
    @Override
    public Single<List<Book>> books(String query) {
        return ApiFactory.getBooksService()
                .books(query)
                .map(SearchResult::getListGBooks)
                .map(this ::convertList)
                .compose(RxUtils.asyncSingle());
    }

    @Override
    public Single<Book> book(String id) {
        return ApiFactory.getBooksService()
                .book(id)
                .map(this::convert)
                .compose(RxUtils.asyncSingle());
    }

    @Override
    public Single<List<Book>> loadDefaultBooks() {
        return ApiFactory.getBooksService()
                .books(DEFAULT_QUERY,DEFAULT_MAX_RESULTS,DEFAULT_ORDER_BOOK,DEFAULT_PROJECTION)
                .map(SearchResult::getListGBooks)
                .map(this ::convertList)
                .compose(RxUtils.asyncSingle());
    }

    private List<Book> convertList(List<GBook> booksApi){
        Log.d(TAG,"books size =  " + booksApi.size());
        List<Book> books = new ArrayList<>();

        for(GBook GBook : booksApi){
            books.add(convert(GBook));
        }
        return books;
    }

    private Book convert(GBook gBook) {
        Book book = new Book();
        book.setId(gBook.getId());

        VolumeInfo vInfo = gBook.getVolumeInfo();

        book.setName(vInfo.getTitle());

        if (vInfo.getAuthors() == null) {
            book.setAuthors(null);
        } else {
            book.setAuthors(Arrays.asList(vInfo.getAuthors()));
        }

        String desc = vInfo.getDescription();
        if (desc == null) {
            book.setDesc(null);
        } else {
            book.setDesc(desc);
        }

        ImageLinks imageLinks = vInfo.getImageLinks();
        if (imageLinks != null) {
            book.setPhotoUrl(imageLinks.getThumbnail());
        } else {
            book.setPhotoUrl(String.valueOf(R.drawable.book_default));
        }

        return book;
    }
}
