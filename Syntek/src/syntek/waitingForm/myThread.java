/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek.waitingForm;

import java.awt.Frame;

/**
 *
 * @author MrOnly
 */
public class myThread extends Thread {

    private String myMsg = "Minh ga";
    public Frame parentObj = null;

    public void setMyMsg(String myMsg) {
        this.myMsg = myMsg;
    }

    public myThread() {
    }
    WaitingDialog frm = null;

    @Override
    public void run() {
        super.run();
        frm = new WaitingDialog(parentObj, true);
        frm.setMsg(myMsg);
        frm.setVisible(true);
    }

    public void StopThread() {
        frm.dispose();
    }

    public static void main(String[] args) {
        myThread th = new myThread();
        th.run();
    }
}
