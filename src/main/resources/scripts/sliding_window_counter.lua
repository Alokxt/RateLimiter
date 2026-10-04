local key = KEYS[1]
local now = tonumber(ARGV[1])
local maxRequests = tonumber(ARGV[2])
local windowSeconds = tonumber(ARGV[3])

local window = redis.call("HMGET", key, "currCount", "currWindow", "prevCount", "start")
local currCount = tonumber(window[1])
local currWindow = tonumber(window[2])
local prevCount = tonumber(window[3])
local start = tonumber(window[4])

if start == nil then
    start = now
    currWindow = 0
    currCount = 0
    prevCount = 0
end

local gap = now - start
local windowIndex = math.floor(gap / windowSeconds)
local rem = gap % windowSeconds

if windowIndex == currWindow + 1 then
    prevCount = currCount
    currCount = 0
    currWindow = windowIndex
elseif windowIndex > currWindow + 1 then
    prevCount = 0
    currCount = 0
    currWindow = windowIndex
end

local prevWeight = 1.0 - (rem / windowSeconds)
local total = prevWeight * prevCount + currCount

local allowed = 0
if total < maxRequests then
    currCount = currCount + 1
    allowed = 1
end

redis.call("HMSET", key, "currCount", currCount, "currWindow", currWindow, "prevCount", prevCount, "start", start)
redis.call("EXPIRE", key, windowSeconds * 2)

return {allowed, tostring(maxRequests - currCount)}