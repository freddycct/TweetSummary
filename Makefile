SHELL=/bin/bash
LC_ALL=C
CP=bin:jar/mysql-connector-java-5.1.20-bin.jar:jar/JFlex.jar:jar/ark-1.0-SNAPSHOT.jar:jar/commons-cli-1.2.jar:jar/commons-math3-3.0.jar:jar/opencsv-2.3.jar
DIR=/mnt/freddy

EVENT_LIST=facebook_ipo obamacare japan bp wallstreet
MODEL_LIST=lda np_lda decay_lda gauss_lda

prec_recall1:
	cat $(DIR)/results/facebook_ipo/K=8/lda/retrieved_tweets_cleaned.txt       | java -cp $(CP) CalculatePrecRecall | uniq > $(DIR)/results/facebook_ipo/K=8/lda/prec_recall.txt &
	cat $(DIR)/results/facebook_ipo/K=8/np_lda/retrieved_tweets_cleaned.txt    | java -cp $(CP) CalculatePrecRecall | uniq > $(DIR)/results/facebook_ipo/K=8/np_lda/prec_recall.txt &
	cat $(DIR)/results/facebook_ipo/K=8/decay_lda/retrieved_tweets_cleaned.txt | java -cp $(CP) CalculatePrecRecall | uniq > $(DIR)/results/facebook_ipo/K=8/decay_lda/prec_recall.txt &
	cat $(DIR)/results/facebook_ipo/K=8/gauss_lda/retrieved_tweets_cleaned.txt | java -cp $(CP) CalculatePrecRecall | uniq > $(DIR)/results/facebook_ipo/K=8/gauss_lda/prec_recall.txt &

prec_recall2:
	cat $(DIR)/results/facebook_ipo/K=8/lda/prec_recall.txt       | java -cp $(CP) CalculatePrecRecall2 | uniq > $(DIR)/results/facebook_ipo/K=8/lda/prec_recall_2.txt &
	cat $(DIR)/results/facebook_ipo/K=8/np_lda/prec_recall.txt    | java -cp $(CP) CalculatePrecRecall2 | uniq > $(DIR)/results/facebook_ipo/K=8/np_lda/prec_recall_2.txt &
	cat $(DIR)/results/facebook_ipo/K=8/decay_lda/prec_recall.txt | java -cp $(CP) CalculatePrecRecall2 | uniq > $(DIR)/results/facebook_ipo/K=8/decay_lda/prec_recall_2.txt &
	cat $(DIR)/results/facebook_ipo/K=8/gauss_lda/prec_recall.txt | java -cp $(CP) CalculatePrecRecall2 | uniq > $(DIR)/results/facebook_ipo/K=8/gauss_lda/prec_recall_2.txt &

prec_recall3:
	cat $(DIR)/results/facebook_ipo/K=8/lda/prec_recall_2.txt       | java -cp $(CP) CalculatePrecRecall3 > results/facebook_ipo/K=8/lda/prec_recall_3.txt &
	cat $(DIR)/results/facebook_ipo/K=8/np_lda/prec_recall_2.txt    | java -cp $(CP) CalculatePrecRecall3 > results/facebook_ipo/K=8/np_lda/prec_recall_3.txt &
	cat $(DIR)/results/facebook_ipo/K=8/decay_lda/prec_recall_2.txt | java -cp $(CP) CalculatePrecRecall3 > results/facebook_ipo/K=8/decay_lda/prec_recall_3.txt &
	cat $(DIR)/results/facebook_ipo/K=8/gauss_lda/prec_recall_2.txt | java -cp $(CP) CalculatePrecRecall3 > results/facebook_ipo/K=8/gauss_lda/prec_recall_3.txt &

json_search:
	for file in `cat remaining_files.txt` ; do \
		cat /home/freddy/data/www.ark.cs.cmu.edu/tweets/raw_hose/$${file} | zcat | java -cp $(CP) JSONSearch | ssh freddy@freddy.socialrankr.net "cat >> $(DIR)/data/josh_users.json" ; \
		echo $${file} >> processed_files.txt ; \
	done

ngrams_corpus:
	n=1 ; while [[ $$n -le 3 ]]; do \
		cut -d $$'\t' -f 3 $(DIR)/data/tweets/sfbay_facebook_ipo.txt | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > $(DIR)/data/tweets/sfbay_facebook_ipo.$${n}grams ; \
		(( n = n + 1 )); \
	done

ngrams_wiki:
	n=1 ; while [[ $$n -le 3 ]]; do \
		cat testset/$(EVENT).wiki | bin/opennlp SentenceDetector bin/en-sent.bin | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > testset/$(EVENT).wiki.$${n}grams; \
		(( n = n + 1 )); \
	done

excel:
	echo "Summary 1" > results/$(EVENT)/K=$(K)/summary.txt
	cut -d $$'\t' -f 2,3 $(DIR)/results/$(EVENT)/K=$(K)/lda/coherent.txt >> results/$(EVENT)/K=$(K)/summary.txt
	echo $$'\n' >> results/$(EVENT)/K=$(K)/summary.txt
	echo "Summary 2" >> results/$(EVENT)/K=$(K)/summary.txt
	cut -d $$'\t' -f 2,3 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/coherent.txt >> results/$(EVENT)/K=$(K)/summary.txt
	echo $$'\n' >> results/$(EVENT)/K=$(K)/summary.txt
	echo "Summary 3" >> results/$(EVENT)/K=$(K)/summary.txt
	cut -d $$'\t' -f 2,3 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/coherent.txt >> results/$(EVENT)/K=$(K)/summary.txt
	echo $$'\n' >> results/$(EVENT)/K=$(K)/summary.txt
	echo "Summary 4" >> results/$(EVENT)/K=$(K)/summary.txt
	cut -d $$'\t' -f 2,3 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/coherent.txt >> results/$(EVENT)/K=$(K)/summary.txt
	echo $$'\n' >> results/$(EVENT)/K=$(K)/summary.txt

count_ngrams:
	n=1 ; while [[ $$n -le 3 ]]; do \
		java -cp $(CP) CountNgram  $(DIR)/results/$(EVENT)/K=$(K)/lda/coherent.$${n}grams testset/$(EVENT).wiki.$${n}grams > $(DIR)/results/$(EVENT)/K=$(K)/coherent_$${n}grams.txt ; \
		java -cp $(CP) CountNgram  $(DIR)/results/$(EVENT)/K=$(K)/np_lda/coherent.$${n}grams testset/$(EVENT).wiki.$${n}grams >> $(DIR)/results/$(EVENT)/K=$(K)/coherent_$${n}grams.txt ; \
		java -cp $(CP) CountNgram  $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/coherent.$${n}grams testset/$(EVENT).wiki.$${n}grams >> $(DIR)/results/$(EVENT)/K=$(K)/coherent_$${n}grams.txt ; \
		java -cp $(CP) CountNgram  $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/coherent.$${n}grams testset/$(EVENT).wiki.$${n}grams >> $(DIR)/results/$(EVENT)/K=$(K)/coherent_$${n}grams.txt ; \
		(( n = n + 1 )); \
	done

ngrams_tweets:
	n=1 ; while [[ $$n -le 3 ]]; do \
		cat $(DIR)/results/$(EVENT)/K=$(K)/lda/coherent.txt | cut -d $$'\t' -f 2 | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > $(DIR)/results/$(EVENT)/K=$(K)/lda/coherent.$${n}grams ; \
		cat $(DIR)/results/$(EVENT)/K=$(K)/np_lda/coherent.txt | cut -d $$'\t' -f 2 | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/coherent.$${n}grams ; \
		cat $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/coherent.txt | cut -d $$'\t' -f 2 | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/coherent.$${n}grams ; \
		cat $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/coherent.txt | cut -d $$'\t' -f 2 | bin/opennlp SimpleTokenizer | java -cp $(CP) GetNgram $${n} | sort | java -cp $(CP) Reduce > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/coherent.$${n}grams ; \
		(( n = n + 1 )); \
	done

summarize_all:
	k=4 ; while [[ $$k -le 12 ]]; do \
		make summarize EVENT=$(EVENT) K=$${k} ; \
		(( k = k + 1 )) ; \
	done

#summarize the tweets using all the models and find their scores
summarize: ngrams_tweets count_ngrams
	mkdir -p results/$(EVENT)/K=$(K)
	java -cp $(CP) Bleu $(DIR) $(EVENT) $(K) > results/$(EVENT)/K=$(K)/coherent_bleu.txt

#delete deprecated files
rm_files:
	for event in $(EVENT_LIST); do \
		k=4 ; while [[ $${k} -le 12 ]]; do \
			for model in $(MODEL_LIST); do \
				#mv results/$${event}/K=$${k}/$${model}/distribution.txt	$(DIR)/results/$${event}/K=$${k}/$${model}/ ; \
				#mv results/$${event}/K=$${k}/$${model}/time.txt $(DIR)/results/$${event}/K=$${k}/$${model}/ ; \
				#rm -rf $(DIR)/results/$${event}/K=$${k}/$${model}/kappa=* ; \
				#rm results/$${event}/K=$${k}/kappa=*_summary.* results/$${event}/K=$${k}/score_*.txt ; \
			done ; \
			(( k = k + 1 )); \
		done ; \
	done

#Look for more tweets from the original corpus using the "trained" model
search_gauss_lda:
	cat $(TWEETS) | java -cp $(CP) gauss_lda.StreamSearch --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/retrieved_tweets.txt

#Look for more tweets from the original corpus using the "trained" model
search_decay_lda:
	cat $(TWEETS) | java -cp $(CP) decay_lda.StreamSearch --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/retrieved_tweets.txt

#Look for more tweets from the original corpus using the "trained" model
search_np_lda:
	cat $(TWEETS) | java -cp $(CP) np_lda.StreamSearch --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/retrieved_tweets.txt

#Look for more tweets from the original corpus using the "trained" model
search_lda:
	cat $(TWEETS) | java -cp $(CP) lda.StreamSearch --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > $(DIR)/results/$(EVENT)/K=$(K)/lda/retrieved_tweets.txt

infer_all: infer_lda infer_np_lda infer_decay_lda infer_gauss_lda

load_all: load_lda load_np_lda load_decay_lda load_gauss_lda

#Loads the model without additional training
load_lda: 
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/lda/
	cat $(DIR)/data/tweets/$(EVENT)_K=$(K).lda | java -cp $(CP) lda.InferModel --iterations=0 --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)"
	k=1 ; while [[ $${k} -le $(K) ]]; do \
		sort -r -g -t$$'\t' -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/time.txt

#Loads the model without additional training
load_np_lda:
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/np_lda/
	cat $(DIR)/data/tweets/$(EVENT)_K=$(K).np_lda | java -cp $(CP) np_lda.InferModel --iterations=0 --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)"
	k=1 ; while [[ $${k} -le $(K) ]]; do \
		sort -r -g -t$$'\t' -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/np_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/np_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/time.txt

#Loads the model without additional training
load_decay_lda:
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/
	cat $(DIR)/data/tweets/$(EVENT)_K=$(K).decay_lda | java -cp $(CP) decay_lda.InferModel --iterations=0 --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)"
	k=1 ; while [[ $${k} -le $(K) ]] ; do \
		sort -r -g -t$$'\t' -T$(HOME)/data -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/time.txt

#Loads the model without additional training
load_gauss_lda:
	mkdir -p results/$(EVENT)/K=$(K)/gauss_lda/
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/
	cat $(DIR)/data/tweets/$(EVENT)_K=$(K).gauss_lda | java -cp $(CP) gauss_lda.InferModel --iterations=0 --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)"
	k=1 ; while [[ $${k} -le $(K) ]] ; do \
		sort -r -g -t$$'\t' -T$(HOME)/data -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/time.txt

#Inference for LDA (Topic Model)
infer_lda:
	mkdir -p results/$(EVENT)/K=$(K)/lda/
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/lda/
	cat $(DIR)/data/tweets/$(EVENT)_init.lda | java -cp $(CP) lda.InferModel --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > results/$(EVENT)/K=$(K)/lda/log.txt
	cut -d : -f 3 results/$(EVENT)/K=$(K)/lda/log.txt > results/$(EVENT)/K=$(K)/lda/mle.txt
	k=1 ; while [[ $${k} -le $(K) ]]; do \
		sort -r -g -t$$'\t' -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/lda/time.txt

#Inference for NounPhrases + Topic Model
infer_np_lda:
	mkdir -p results/$(EVENT)/K=$(K)/np_lda/
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/np_lda/
	cat $(DIR)/data/tweets/$(EVENT)_init.np | java -cp $(CP) np_lda.InferModel --prefix_dir=$(DIR) --num_topics=$(K) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > results/$(EVENT)/K=$(K)/np_lda/log.txt
	cut -d : -f 3 results/$(EVENT)/K=$(K)/np_lda/log.txt > results/$(EVENT)/K=$(K)/np_lda/mle.txt
	k=1 ; while [[ $${k} -le $(K) ]]; do \
		sort -r -g -t$$'\t' -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/np_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/np_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/np_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/np_lda/time.txt

#Inference for the Decay Topic Model
infer_decay_lda:
	mkdir -p results/$(EVENT)/K=$(K)/decay_lda/
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/
	cat $(DIR)/data/tweets/$(EVENT)_init.np | java -cp $(CP) decay_lda.InferModel --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > results/$(EVENT)/K=$(K)/decay_lda/log.txt
	cut -d : -f 3 results/$(EVENT)/K=$(K)/decay_lda/log.txt > results/$(EVENT)/K=$(K)/decay_lda/mle.txt
	k=1 ; while [[ $${k} -le $(K) ]] ; do \
		sort -r -g -t$$'\t' -T$(HOME)/data -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/decay_lda/time.txt

#Inference for the Gaussian Topic Model
infer_gauss_lda:
	mkdir -p results/$(EVENT)/K=$(K)/gauss_lda/
	mkdir -p $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/
	cat $(DIR)/data/tweets/$(EVENT)_init.np | java -cp $(CP) gauss_lda.InferModel --prefix_dir=$(DIR) --num_topics=$(K) --decay=$(DECAY) --experiment_name=$(EVENT) --ignore_words="$(IGNORE)" > results/$(EVENT)/K=$(K)/gauss_lda/log.txt
	cut -d : -f 3 results/$(EVENT)/K=$(K)/gauss_lda/log.txt > results/$(EVENT)/K=$(K)/gauss_lda/mle.txt
	k=1 ; while [[ $${k} -le $(K) ]] ; do \
		sort -r -g -t$$'\t' -T$(HOME)/data -k1,1 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/topic\_$${k}.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/tmp ; \
		mv $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/tmp $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/topic\_$${k}.txt ; \
		((k = k + 1)) ; \
	done
	cut -d $$'\t' -f 1 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/distribution.txt
	cut -d $$'\t' -f 4 $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/transition.txt > $(DIR)/results/$(EVENT)/K=$(K)/gauss_lda/time.txt

#This rule is for tokenizing and finding noun phrases in tweets
find_np:
	cat $(DIR)/data/tweets/$(DATA)_$(EVENT).txt | java -cp $(CP) np_lda.TagTweets  > $(DIR)/data/tweets/$(EVENT)_init.np
	cat $(DIR)/data/tweets/$(DATA)_$(EVENT).txt | java -cp $(CP) lda.TagTweets2 > $(DIR)/data/tweets/$(EVENT)_init.lda

compile:
	mkdir -p bin/
	javac -d bin/ -cp $(CP) src/org/json/*.java
	javac -d bin/ -cp $(CP) src/*.java
	javac -d bin/ -cp $(CP) src/lda/*.java
	javac -d bin/ -cp $(CP) src/np_lda/*.java
	javac -d bin/ -cp $(CP) src/decay_lda/*.java
	javac -d bin/ -cp $(CP) src/gauss_lda/*.java
	javac -d bin/ -cp $(CP) src/visualize/*.java

