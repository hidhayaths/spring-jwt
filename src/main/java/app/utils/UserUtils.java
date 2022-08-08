package app.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class UserUtils {

    public static User loggedInUser(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String loggedInUsername(){
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
