/**
 * Created by hiephn on 2014/10/13.
 */
define(['jquery'], function($) {
    var learnItemTemplate = {
            id: -1,
            name: null,
            meaning: null,
            pronun: null,
            type: null,
            audio: null,
            penaltyRate: null,
            reviseTimeLeft: 0,
            questions: [{
                text: null,
                type: null,
                answer: null,
                options: [null, null],
                rightNo: 0,
                wrongNo: 0,
                // Helper attributes from here
                correctItemPtr: null,
                userAnswer: null,
                isSubmitted: null,
                isUserCorrect: null,
                qId: null, // Format: [item id]-[auto incremental number]
                point: null
            }],
            // Helper attributes from here
            lastAnswer: null,
            isLearned: null,
            right: 0,
            wrong: 0
        },
        DEFAULT_LEARNING_ITEMS_LENGTH = 2,
        QUESTIONS_TO_REMOVE_WHEN_WRONG = 2,
        ITEM_LEARNING_POINT = 10,
        ITEM_LEARNING_POINTS = [10, 15, 20, 25, 30, 35],
        ITEM_REVISING_POINT = 10;
        ITEM_REVISING_POINTS = [50, 100, 200, 350, 550, 800];

    function LearnTurn(learnedItemsNum) {
        this.learnItemArray = learnedItemsNum;
        this.curQuestionPtr = null;
        this.curLearnItems = [];
        this.curLearnQuestions = [];

        // Add helper attributes
        this.learnItemArray.forEach(function(learnItem) {
            learnItem.lastAnswer = null;
            learnItem.isLearned = false;
            learnItem.right = 0;
            learnItem.wrong = 0;
            learnItem.result = {
                map: '',
                history: []
            };
            var qi = 0;
            learnItem.questions.forEach(function(question) {
                question.correctItemPtr = learnItem;
                question.userAnswer = null;
                question.isSubmitted = null;
                question.qId = learnItem.id + '-' + qi++;
                question.point = null;
                question.bonus = null;
                question.penalty = null;
            });
        });
        this.result = {
            right: 0,
            wrong: 0,
            point: 0,
            map: '',
            history: []
        };
    }

    $.extend(LearnTurn.prototype, {
        getLearnedItemsNum: function() {
            var num = 0;
            this.learnItemArray.forEach(function(learnItem) {
                num += learnItem.isLearned ? 1 : 0;
            });
            return num;
        },
        getLearnedItems: function() {
            var learnedItems = [];
            this.learnItemArray.forEach(function(learnItem) {
                if (learnItem.isLearned) {
                    learnedItems.push(learnItem);
                }
            });
            return learnedItems;
        },
        isExpired: function(item) {
            return item.questions.length == 0;
        },
        getExpiredItemsNum: function() {
            var expiredNum = 0,
                that = this;
            this.learnItemArray.forEach(function(learnItem) {
                expiredNum += that.isExpired(learnItem) ? 1 : 0;
            });
            return expiredNum;
        },
        getAvailableLearnedItems: function() {
            var availableLearnedItems = [],
                that = this;
            this.learnItemArray.forEach(function(learnItem) {
                if (learnItem.isLearned && !that.isExpired(learnItem)) {
                    availableLearnedItems.push(learnItem);
                }
            });
            return availableLearnedItems;
        },
        learnItemNext: function(num, isRevise) {
            this.curLearnItems = [];
            var curLearnItems = this.curLearnItems;
            num = num ? num : 1;
            this.learnItemArray.forEach(function(learnItem) {
                if (!learnItem.isLearned && num > 0) {
                    if (!isRevise) {
                        curLearnItems.push(learnItem);
                    }
                    learnItem.isLearned = true;
                    --num;
                }
            });
        },
        learnQuestionNext: function() {
            this.curLearnQuestions = [];
            var avaiLearnedItems = this.getAvailableLearnedItems(),
                minPosQuestNum = avaiLearnedItems.length,
                maxPosQuestNum = this.getMaxPossibleQuestionsNum(avaiLearnedItems),
                randomizedQuestNum = 0,
                questions = this.curLearnQuestions;

            if (this.getLearnedItemsNum() == this.learnItemArray.length) {
                // If there is no more learning item, quiz session consumes all of the question
                randomizedQuestNum = maxPosQuestNum;
            } else {
                // Number of question will be randomized
                randomizedQuestNum = Math.floor((Math.random() * (maxPosQuestNum - minPosQuestNum)) + minPosQuestNum)
            }

            // Get 1 question from each items
            for (var i = 0; i < avaiLearnedItems.length; ++i) {
                var question = avaiLearnedItems[i].questions.pop();
                questions.push(question);
                --randomizedQuestNum;
                if (this.isExpired(avaiLearnedItems[i])) {
                    avaiLearnedItems.splice(i, 1);
                    --i;
                }
            }

            // Get remaining questions
            while (randomizedQuestNum > 0) {
                var randomizedItemIdx = Math.floor((Math.random() * avaiLearnedItems.length));
                var question = avaiLearnedItems[randomizedItemIdx].questions.pop();
                questions.push(question);
                --randomizedQuestNum;
                if (this.isExpired(avaiLearnedItems[randomizedItemIdx])) {
                    avaiLearnedItems.splice(randomizedItemIdx, 1);
                }
            }
        },
        getMaxPossibleQuestionsNum: function(items) {
            var max = 0;
            items.forEach(function(item) {
                max += item.questions.length;
            });
            return max;
        },
        isGameOver: function() {
            return this.learnItemArray.length == this.getExpiredItemsNum()
                && this.curLearnItems.length == 0
                && this.curLearnQuestions.length == 0;
        },
        startLearnSessionIfNeeded: function(isRevise) {
            if (this.curLearnItems.length == 0
                && this.curLearnQuestions.length == 0) {
                // Start of a new learn/quiz session
                this.learnItemNext(DEFAULT_LEARNING_ITEMS_LENGTH, isRevise);
                this.learnQuestionNext();
            }
        },
        submitAnswer: function(userAnswer) {
            userAnswer = userAnswer == null ? '' : userAnswer;
            this.curQuestionPtr.userAnswer = userAnswer;
            this.curQuestionPtr.isUserCorrect = this.curQuestionPtr.answer.toUpperCase() == userAnswer.toUpperCase();
            var curItem = this.curQuestionPtr.correctItemPtr;
            var history = {question: this.curQuestionPtr};
            curItem.result.history.push(history);
            this.result.history.push(history);

            if (this.curQuestionPtr.isUserCorrect) {
                ++curItem.right;
                curItem.result.map += '1';
                ++this.result.right;
                this.result.map += '1';
                if (curItem.reviseTimeLeft) {
                    // revise turn
                    var originPoint = ITEM_REVISING_POINTS[curItem.reviseNo],
                        bonusRate = Math.max(0, (20 - 5*Math.floor(-curItem.reviseTimeLeft / 60 / 30))/100),
                        bonusPoint = originPoint * bonusRate,
                        penaltyPoint = originPoint * curItem.penaltyRate,
                        finalPoint = originPoint + bonusPoint - penaltyPoint;

                    this.result.point += finalPoint;
                    this.curQuestionPtr.point = finalPoint;
                    this.curQuestionPtr.bonus = bonusPoint;
                    this.curQuestionPtr.penalty = penaltyPoint;
                } else {
                    // learning turn
                    var learnPointIdx = curItem.right + curItem.rightNo - 1;
                    if (learnPointIdx > 0 && curItem.result.map.length > 1
                        && curItem.result.map[curItem.result.map.length - 2] == '0') {
                        // if last answer is wrong, point is backward
                        --learnPointIdx;
                    }
                    this.result.point += ITEM_LEARNING_POINTS[learnPointIdx];
                    this.curQuestionPtr.point = ITEM_LEARNING_POINTS[learnPointIdx];
                }
            } else {
                ++curItem.wrong;
                curItem.result.map += '0';
                ++this.result.wrong;
                this.result.map += '0';
                this.curQuestionPtr.point = 0;
                curItem.lastAnswer = userAnswer;
                // Display this item again
                this.curLearnItems.unshift(curItem);

                // Reduce number of question of the item
                var questionsToRemove = QUESTIONS_TO_REMOVE_WHEN_WRONG;
                // Remove questions that are not yet extracted
                while (questionsToRemove > 0 && curItem.questions.length > 0) {
                    curItem.questions.splice(0, 1);
                    --questionsToRemove;
                    console.log('----removed 1 question from item ' + curItem.id);
                }
                // Remove learn question
                for (var i = 0; questionsToRemove > 0 && i < this.curLearnQuestions.length; ++i) {
                    if (curItem.id == this.curLearnQuestions[i].correctItemPtr.id) {
                        this.curLearnQuestions.splice(i, 1);
                        --i;
                        --questionsToRemove;
                        console.log('----removed 1 question from learn question of item ' + curItem.id);
                    }
                }
            }
        },
        accuracy: function() {
            return Math.round(this.result.right / (this.result.right + this.result.wrong) * 100);
        }
    });

    return LearnTurn;
});
