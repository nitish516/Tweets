package com.example.nitish.tweets;

import java.io.IOException;
import java.util.List;

/**
 * Created by nitish on 25/06/16.
 */
public interface HttpService {
    List<Tweet> getData() throws IOException;
}
