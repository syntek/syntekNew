/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek.waitingForm;

/**
 *
 * @author MrOnly
 */
public class myThread extends Thread{

    public myThread() {
    }
    waiting_form frm = null;
    @Override
    public void run() {
        super.run(); 
        frm = new waiting_form();
        frm.setVisible(true);
    }
    public void StopThread()
    {
        frm.dispose();
    }
}
