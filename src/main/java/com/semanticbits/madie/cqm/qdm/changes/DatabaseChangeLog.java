package com.semanticbits.madie.cqm.qdm.changes;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

@ChangeLog
public class DatabaseChangeLog {

//    @ChangeSet(order = "001", id = "seedDatabase", author = "Sai")
//    public void seedDatabase(ExpenseRepository expenseRepository) {
//        List<Expense> expenseList = new ArrayList<>();
//        expenseList.add(createNewExpense("Movie Tickets", ENTERTAINMENT, BigDecimal.valueOf(40)));
//        expenseList.add(createNewExpense("Dinner", RESTAURANT, BigDecimal.valueOf(60)));
//        expenseList.add(createNewExpense("Netflix", ENTERTAINMENT, BigDecimal.valueOf(10)));
//        expenseList.add(createNewExpense("Gym", MISC, BigDecimal.valueOf(20)));
//        expenseList.add(createNewExpense("Internet", UTILITIES, BigDecimal.valueOf(30)));
//
//        expenseRepository.insert(expenseList);
//    }

//    @ChangeSet(order = "001", id = "changeWithMongockTemplate", author = "mongock")
//    public void changeWithMongockTemplate(MongockTemplate mongockTemplate) {
//        mongockTemplate.save(new MyEntity());
//    }

    @ChangeSet(order = "01", id = "changeSetForDevOnly", author = "mongock")
    public void changeSetForDevOnly(MongoDatabase db){
        db.createCollection("HELLO");
    }
}
