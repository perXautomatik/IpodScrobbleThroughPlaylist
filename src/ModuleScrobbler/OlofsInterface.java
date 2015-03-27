/*gameFrame3 = new JFrame();
        gameFrame3.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;
        gameFrame3.setSize(550, 250);
        gameFrame3.setTitle("Quiz Offline");

        questionLabel = new JLabel(InfoStorer.gameList.get(InfoStorer._nextQ).getQuestion());
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weighty = 1;
        gameFrame3.add(questionLabel, gc);

        points = new JLabel(InfoStorer._points + " / " + "10");
        gc.fill = GridBagConstraints.NONE;
        gc.gridy = 1;
        gameFrame3.add(points, gc);

        timerLabel = new JLabel("Tid kvar: " + counter + " sekunder");
        gc.gridy = 2;
        gc.ipadx = 0;
        gameFrame3.add(timerLabel, gc);

        answerField = new JTextField(30);
        gc.weighty = 0;
        gc.gridy = 3;
        gc.ipadx = 300;
        gameFrame3.add(answerField, gc);

        nextQB = new JButton("Nästa fråga");
        gc.gridy = 4;
        gc.ipadx = 205;
        gameFrame3.add(nextQB, gc);
        gameFrame3.setVisible(true);
        b4.addActionListener(new ActionListener(){
@Override
public void actionPerformed(ActionEvent e){
        HighScoreFrame(); //A method that initiates the high score.
        }});


        */