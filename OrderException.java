/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment6_2;

/**
 *
 * @author Pranay
 */
@SuppressWarnings("serial")
public class OrderException extends Exception {

    String errorMessage = "";

    public OrderException(String s) {
        super(s);
        errorMessage = s;
    }

    public void getMessage(String s) {
        System.out.println(errorMessage);
    }

}
