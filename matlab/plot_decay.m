%Display how delta affects the decay
second = 1000;
minute = 60 * second;
hour = 60 * minute;
day = 24 * hour;
week = 7 * day;
month = 4 * week;

colors = lines(7);
time_diff = [second:second:minute-second, minute:minute:hour-minute, hour:hour:day-hour, day:day:week-day, week:week:month];
log_time_diff = log(time_diff);
deltas = [-1e-3, -1e-4, -1e-5, -1e-6, -1e-7, -1e-8, -1e-9];
figure;hold on;
legend_str = cell(7, 1);
for i=1:7
    plot( log_time_diff, 100 .* exp( deltas(i) .* time_diff ), 'Color', colors(i, :) );
    legend_str(i) = { sprintf('delta = %e', deltas(i)) };
end
xlabel('log(Time Difference in ms)', 'FontSize', 16);
ylabel('% Decay', 'FontSize', 16);

legend(legend_str, 'FontSize', 8, 'Location', 'NorthEast');

plot(log([second, second]), [0 100], '--');
plot(log([minute, minute]), [0 100], '--');
plot(log([hour  , hour])  , [0 100], '--');
plot(log([day   , day])   , [0 100], '--');
plot(log([week  , week])  , [0 100], '--');
plot(log([month , month]) , [0 100], '--');
plot(xlim, [50 50], ':');

text(log(second), 50, 'second', 'FontSize', 16);
text(log(minute), 50, 'minute', 'FontSize', 16);
text(log(hour), 50, 'hour', 'FontSize', 16);
text(log(day), 50, 'day', 'FontSize', 16);
text(log(week), 50, 'week', 'FontSize', 16);
text(log(month), 50, 'month', 'FontSize', 16);
hold off;
%end

saveas(gcf, '../latex/eps/decay_chart.eps', 'psc2');