/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

/**
 *
 * @author SenetUser
 */
import java.util.Random;

public class Bank {
    private final double[] accounts;

    public Bank(int n, double initBalance) {
        accounts = new double[n];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = initBalance;
        }
    }

    public int size() {
        return accounts.length;
    }

    public synchronized double getTotalBalance() {
        double total = 0;
        for (int i = 0; i < accounts.length; i++) {
            total += accounts[i];
        }
        return total;
    }

    public synchronized void transfer(int from, int to, double amount) throws InterruptedException {
        while (accounts[from] < amount) {
            System.out.println(Thread.currentThread().getName() + " đợi đủ tiền");
            wait();
            System.out.println(Thread.currentThread().getName() + " tiếp tục giao dịch");
        }
        accounts[from] -= amount;
        accounts[to] += amount;
        System.out.println("Chuyển " + amount + " từ account " + from + " sang account " + to);
        System.out.println("Tổng tiền của các account: " + getTotalBalance());
        notifyAll();
    }

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank(100, 1000);
        int size = bank.size();
        for (int i = 0; i < size; i++) {
            TransferMoney transferMoney = new TransferMoney(bank, i, 1000);
            Thread thread = new Thread(transferMoney);
            thread.start();
        }
    }
}

class TransferMoney implements Runnable {
    private final Bank bank;
    private final int fromAcc;
    private final double maxAmount;
    private final int delay = 1000;

    public TransferMoney(Bank bank, int fromAcc, double maxAmount) {
        this.bank = bank;
        this.fromAcc = fromAcc;
        this.maxAmount = maxAmount;
    }

    @Override
    public void run() {
        Random rd = new Random();
        int toAcc = 0;
        double amount = 0;
        try {
            while (true) {
                do {
                    toAcc = rd.nextInt(bank.size());
                } while (toAcc == fromAcc);
                amount = rd.nextInt((int) maxAmount);
                bank.transfer(fromAcc, toAcc, amount);
                Thread.sleep(rd.nextInt(delay));
            }
        } catch (InterruptedException ex) {
            System.out.println("Giao dịch chuyển tiền từ account " + fromAcc + " sang account " + toAcc + " bị gián đoạn");
        }
    }
}
