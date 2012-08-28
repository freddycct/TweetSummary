clear;
events = [{'facebook_ipo'}, {'s3'}];
e = 1;

words = [{'a'}, {'to'}, {'the'}, {'facebook'}, {'nasdaq'}, {'price'}, {'$28'}, {'$35'}, {'$38'}, {'citizenship'}, {'saverin'}, {'date'}, {'may'}, {'17'}, {'18'}, {'sued'}, {'lawsuits'}, {'glitches'}];
all_words_time = load(sprintf('../results/%s/all_words_time.txt', events{e}));

init_dates;

time_series = begin_time:day:end_time;
time_bins = (time_series(1:end-1) + time_series(2:end)) ./ 2;

all_words_frequency = hist(all_words_time, time_bins);

figure('visible', 'off');hold on;
bar(time_bins, all_words_frequency);
xlabel('Time (ms)', 'FontSize', 16);
ylabel('Occurrences', 'FontSize', 16);
title('All Words', 'FontSize', 16);

ylimits = ylim;
plot([apr_01 apr_01], ylim, '--');
plot([may_01 may_01], ylim, '--');
plot([may_18 may_18], ylim, '--');
plot([jun_01 jun_01], ylim, '--');

text(apr_01 + day, ylimits(2) * 0.75, 'April 1st 2012', 'FontSize', 10);
text(may_01 + day, ylimits(2) * 0.75, 'May 1st 2012', 'FontSize', 10);
text(may_18 + day, ylimits(2) * 0.95, 'May 18th  2012', 'FontSize', 10);
text(jun_01 + day, ylimits(2) * 0.75, 'June 1st 2012', 'FontSize', 10);
saveas(gcf, '../latex/eps/all_words.eps', 'psc2');
hold off;
close(gcf);

for i=1:length(words)
    word_time = load( sprintf('../results/%s/%s_time.txt', events{e}, words{i} ));
    word_frequency = hist(word_time, time_bins);
    
    figure('visible', 'off');hold on;
    bar(time_bins, word_frequency);
    xlabel('Time (ms)', 'FontSize', 16);
    ylabel('Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    ylimits = ylim;
    
    plot([apr_01 apr_01], ylim, '--');
    plot([may_01 may_01], ylim, '--');
    plot([may_18 may_18], ylim, '--');
    plot([jun_01 jun_01], ylim, '--');
    
    text(apr_01 + day, ylimits(2) * 0.75, 'April 1st 2012', 'FontSize', 10);
    text(may_01 + day, ylimits(2) * 0.75, 'May 1st 2012', 'FontSize', 10);
    text(may_18 + day, ylimits(2) * 0.95, 'May 18th  2012', 'FontSize', 10);
    text(jun_01 + day, ylimits(2) * 0.75, 'June 1st 2012', 'FontSize', 10);
    
    saveas(gcf, sprintf('../latex/eps/freq_%s.eps', words{i}), 'psc2');
    hold off;
    close(gcf);
end

for i=1:length(words)
    word_time = load( sprintf('../results/%s/%s_time.txt', events{e}, words{i} ));
    word_frequency = hist(word_time, time_bins);
    word_normalized = word_frequency ./ (all_words_frequency + eps);
    
    figure('visible', 'off');hold on;
    bar(time_bins, word_normalized);
    xlabel('Time (ms)', 'FontSize', 16);
    ylabel('Normalized Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    ylimits = ylim;
    
    plot([apr_01 apr_01], ylim, '--');
    plot([may_01 may_01], ylim, '--');
    plot([may_18 may_18], ylim, '--');
    plot([jun_01 jun_01], ylim, '--');
    
    text(apr_01 + day, ylimits(2) * 0.75, 'April 1st 2012', 'FontSize', 10);
    text(may_01 + day, ylimits(2) * 0.75, 'May 1st 2012', 'FontSize', 10);
    text(may_18 + day, ylimits(2) * 0.95, 'May 18th  2012', 'FontSize', 10);
    text(jun_01 + day, ylimits(2) * 0.75, 'June 1st 2012', 'FontSize', 10);
    
    saveas(gcf, sprintf('../latex/eps/norm_%s.eps', words{i}), 'psc2');
    hold off;
    close(gcf);
end

%{
for i=1:length(words)
    word_time = load( sprintf('../results/%s/%s_time.txt', events{e}, words{i} ));
    word_frequency = hist(word_time, time_bins);
    word_normalized = word_frequency ./ (all_words_frequency + eps);
    
    figure
    subplot(2,1,1);
    bar(time_bins, word_frequency);
    xlabel('Time (ms)', 'FontSize', 16);
    ylabel('Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    subplot(2,1,2);
    bar(time_bins, word_normalized);
    xlabel('Time (ms)', 'FontSize', 16);
    ylabel('Normalized Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    sorted_word_normalized = sort(word_normalized, 'descend');
    p_d = sum(word_frequency > 0) / length(word_frequency);
    p_x = sum(sorted_word_normalized(1:7)) * (log (1/p_d))^(9/10);
    fprintf('P(%s) = %f\n', words{i}, p_x);  
end
%}

%{
all_words_count = load(sprintf('../results/%s/all_words_count.txt', events{e}));

begin_count = min(all_words_count);
end_count = max(all_words_count);

count_series = begin_count:end_count;
count_bins = (count_series(1:end-1) + count_series(2:end)) ./ 2;

all_words_frequency = hist(all_words_count, count_bins);

for i=1:length(words)
    word_count = load( sprintf('../results/%s/%s_count.txt', events{e}, words{i} ));
    word_frequency = hist(word_count, count_bins);
    word_normalized = word_frequency ./ (all_words_frequency + eps);
    
    figure
    subplot(2,1,1);
    bar(count_bins, word_frequency);
    xlabel('Count', 'FontSize', 16);
    ylabel('Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    subplot(2,1,2);
    bar(count_bins, word_normalized);
    xlabel('Count', 'FontSize', 16);
    ylabel('Normalized Occurrences', 'FontSize', 16);
    title(sprintf('%s', words{i}), 'FontSize', 16);
    
    p_d = sum(word_frequency > 0) / length(word_frequency);
    p_x = sum(word_normalized) * (log (1/p_d))^3;
    fprintf('P(%s) = %f\n', words{i}, p_x);
end
%}
