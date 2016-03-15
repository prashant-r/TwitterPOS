import sys
import math
import pickle
import copy
import re
from string import Template
from collections import defaultdict
from vit import viterbi
from time import time
class RecordedState(object):
    phi=None
    alpha=None
    pos_tags = None
    words = None;
    strings = None;
    strings_abr = None;
    regExp = None;

Words = defaultdict(int)
phi = defaultdict(int)
alpha = defaultdict(int)
alpha_average = defaultdict(tuple) #(total sum, example number of last update, value of last update)
possible_tags = []
strings = []
strings_abr = []
regExp = defaultdict(str)

T_DEFAULT = 18
add_factor = 1

def get_tags():
    global possible_tags
    tags = defaultdict(bool)
    data = open("data/oct27.train", 'r')
    line = data.readline()
    while line:
        l = line.strip()
        if l:
            vals = re.split(r'\t+', l.rstrip('\t'))
            if(len(vals) == 2):
                tags[vals[1]]
                Words[vals[0]] += 1
            else:
                tags[vals[0]];
                Words['']+=1;
        line = data.readline()
    for t in tags:
        possible_tags.append(t)
    data.close()

def get_strings():
    global strings
    global strings_abr
    string = Template('w_i=$w_i,t=$t')
    strings.append(copy.deepcopy(string))
    string = Template('t_-2=$t_2,t_-1=$t_1,t=$t')
    strings.append(copy.deepcopy(string))
    strings_abr.append(copy.deepcopy(string))
    string = Template('t_-1=$t_1,t=$t')
    strings.append(copy.deepcopy(string))
    strings_abr.append(copy.deepcopy(string))
    string = Template('t=$t')
    strings.append(copy.deepcopy(string))

def get_regExp():
    firstCaps = re.compile('[A-Z].+')
    firstLetterAt = re.compile('@.+')
    AllCaps = re.compile('[A-Z]+$')
    containsDigit = re.compile('.+[1-9].+')
    startNumerals = re.compile('\d+')
    regExp[firstCaps] = 'firstCaps'
    regExp[AllCaps] = 'AllCaps'
    regExp[startNumerals] = 'startNumerals'
    regExp[firstLetterAt] = 'firstLetterIsAtSign'
    regExp[containsDigit] = 'containsDigit'


def get_sentence_and_tags(data):
    sentence = []
    tags = []
    line = data.readline()
    l = line.strip()
    if not line:
        return 0
    while l:
        vals = re.split(r'\t+', l.rstrip('\t'))
        if(len(vals) == 2):
            sentence.append(vals[0])
            tags.append(vals[1])
        else:
            sentence.append('')
            tags.append(vals[0])
        line = data.readline()
        l = line.strip()
    return (copy.deepcopy(sentence), copy.deepcopy(tags))

def get_alpha_indices(strings, d, examp_num):
    positions = []
    for s in strings:
        index = phi.get(s.substitute(d), -1)
        if index == -1:
            index = len(phi)
            phi[s.substitute(d)] = index
            alpha[index]
            alpha_average[index] = (0, examp_num, 0)
        positions.append(index)
    return copy.deepcopy(positions)

def get_indices(sentence, tags, examp_num):
    global strings
    global strings_abr
    result = []
    for i in range(len(sentence)):
##########Strings, trigram model############
        if i == 0:
            d = dict(w_i = sentence[0], t_2 = '*', t_1 = '*', t = tags[0])
            result += get_alpha_indices(strings, d, examp_num)
            d = dict(t_2 = '*', t_1 = '*', t = tags[0])
            result += get_alpha_indices(strings_abr, d, examp_num)
        elif i == 1:
            d = dict(w_i = sentence[1], t_2 = '*', t_1 = tags[0], t = tags[1])
            result += get_alpha_indices(strings, d, examp_num)
            d = dict(t_2 = '*', t_1 = tags[0], t = tags[1])
            result += get_alpha_indices(strings_abr, d, examp_num)
        else:
            d = dict(w_i = sentence[i], t_2 = tags[i-2], t_1 = tags[i-1], t = tags[i])
            result += get_alpha_indices(strings, d, examp_num)
            d = dict(t_2 = tags[i-2], t_1 = tags[i-1], t = tags[i])
            result += get_alpha_indices(strings_abr, d, examp_num)
##############regular Expressions############
        for j in regExp:
            if j.match(sentence[i]):
                phrase = 'w_i={0},t={1}'.format(regExp[j],tags[i])
                index = phi.get(phrase, -1)
                if index == -1:
                    index = len(phi)
                    phi[phrase] = index
                    alpha[index]
                    alpha_average[index] = (0, examp_num, 0)
                result.append(index)

##############capitalization features#############


    if len(sentence) == 1:
        d = dict(t_2 = '*', t_1 = tags[len(tags)-1], t = 'STOP')
    else:
        d = dict(t_2 = tags[len(tags)-2], t_1 = tags[len(tags)-1], t = 'STOP')
    result += get_alpha_indices(strings_abr, d, examp_num)
    return copy.deepcopy(result)

def get_phi():
    data = open("data/oct27.train", 'r')
    vals = get_sentence_and_tags(data)
    counter = 0;

    while vals:
        get_indices(vals[0], vals[1], 1)
        vals = get_sentence_and_tags(data)
        counter = counter + 1;
    print(counter);
    data.close()

def perceptron(print_alpha = 0):
    global possible_tags
    global strings
    global strings_abr
    global add_factor
    get_regExp()
    get_strings()
    get_tags()
    get_phi()

    for t in range(T_DEFAULT):
        print('---{0}---'.format(t))
        sys.stdout.flush()
        dont_repeat = True
        data = open("data/oct27.train", 'r')
        vals = get_sentence_and_tags(data)
        j = 0
        incorrect =0
        examp_num = 0
        while vals:
            examp_num += 1
            sentence = vals[0]
            correct_tags = vals[1]
            tags = viterbi(sentence, phi, possible_tags, alpha, strings, strings_abr, Words, regExp, False)
            indices = get_indices(sentence, tags, examp_num)
            correct_indices = get_indices(sentence, correct_tags, examp_num)
            if not tags == correct_tags:
                incorrect+=1
                dont_repeat = False
                for i in indices:
                    alpha[i] += -1*add_factor
                for i in correct_indices:
                    alpha[i] += add_factor
            else:
                j += 1
            for i in set(indices) | set(correct_indices):
                val1 = alpha_average[i][0]+(examp_num - alpha_average[i][1])*alpha_average[i][2]
                val2 = examp_num
                val3 = alpha[i]
                alpha_average[i] = (val1,val2,val3)
            vals = get_sentence_and_tags(data)
        data.close()
        if dont_repeat:
            print('SUCCESS!!!')
            break
        print('number correct: {0}/{1}'.format(j, j+incorrect))
        for i in alpha:
            val1 = alpha_average[i][0]+(examp_num+1 - alpha_average[i][1])*alpha_average[i][2]
            val2 = 1
            val3 = alpha[i]
            alpha_average[i] = (val1,val2,val3)
        print(t);
        print(T_DEFAULT);
        print(t == T_DEFAULT);
        if(t == T_DEFAULT-1):
            write_alpha()

def write_alpha():
    global alpha_average
    RecordedState={'phi':phi,'alpha':alpha, 'pos_tags': possible_tags, 'words': Words, 'strings': strings, "strings_abr": strings_abr, "regExp" : regExp}
    with open('outputs/RecordedState.pickle', 'wb') as f:
	   pickle.dump(RecordedState, f)

def predict(sentence):
    with open('outputs/RecordedState.pickle', 'rb') as f:
	   RecState = pickle.load(f)
    possible_tags = RecState['pos_tags'];
    alpha = RecState['alpha'];
    phi = RecState['phi']
    Words = RecState['words'];
    strings = RecState['strings'];
    strings_abr = RecState['strings_abr']
    regExp = RecState['regExp']
    tags = viterbi(sentence.split(), phi, possible_tags, alpha, strings, strings_abr, Words, regExp, True)
    print(tags);

def eval():

    with open('outputs/RecordedState.pickle', 'rb') as f:
	   RecState = pickle.load(f)
    data = open("data/oct27.test", 'r')
    vals = get_sentence_and_tags(data)
    alpha = RecState['alpha'];
    phi = RecState['phi']
    Words = RecState['words'];
    strings = RecState['strings'];
    strings_abr = RecState['strings_abr']
    regExp = RecState['regExp']
    possible_tags = RecState['pos_tags']
    correct =0;
    total =0;
    while vals:
        tags = viterbi(vals[0], phi, possible_tags, alpha, strings, strings_abr, Words, regExp, True)
        print(total);
        total = total+1;
        print(tags)
        print(vals[1])
        if (tags == vals[1]):
            correct = correct + 1;
        vals = get_sentence_and_tags(data)

    print("Print Statistics--------");
    print("correct : ", correct);
    print("total : " , total);
    print("accuracy :", correct/total);


def main():
    choice=raw_input("train or Predict or Eval. Enter T or P or E.")
    if(choice == 'T'):
        perceptron(1);
    elif(choice == 'E'):
        eval();
    else:
        sentence = raw_input("Enter your sentence..");
        predict(sentence);

main();


