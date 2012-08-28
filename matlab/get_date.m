function [str] = get_date(ms)
import java.sql.Timestamp
ts = Timestamp(ms);
str = char(ts.toGMTString);
str = str(1:11);
end
