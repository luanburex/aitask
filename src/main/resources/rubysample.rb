require 'json'
# TIME_FORMAT_PATH = "%Y-%m-%d %H-%M-%S"
# .strftime(Constants::TIME_FORMAT_PATH)
STDOUT.puts 'args:'<<ARGV.to_s

exedata = JSON.parse(File.open(ARGV[0], 'r:UTF-8').read)


STDOUT.puts 'scr file:'<<exedata['script']
STDOUT.puts 'rst file:'<<exedata['result']
STDOUT.puts 'log file:'<<exedata['log']

log = []
exedata['datalist'].each do |data|
  dataId = data['dataId']
  # detailId 有顺序么? 和Step是一回事儿不?
  detailmap = exedata['detaillist'][dataId]

  caselog = {}
  caselog['result'] = 'someresult'
  caselog['groupName'] = ''
  caselog['scriptName'] = ''
  caselog['scriptType'] = ''
  caselog['dataDesc'] = ''
  caselog['dataId'] = dataId
  caselog['keyEclapse'] = ''
  caselog['endTime'] = Time.new
  caselog['log'] = 'caselog'
  caselog['business'] = ''
  caselog['dataName'] = data['dataName']
  caselog['startTime'] = Time.new
  caselog['groupNo'] = ''
  caselog['firstExecuteTime'] = ''
  caselog['system'] = ''
  caselog['dataTags'] = data['tags']
  caselog['scriptId'] = data['scriptId']
  caselog['executeNu'] = ''

  steplog = []
  for i in 0..1
    step = {}
    step['startTime'] = Time.new
    step['result'] = 'result'
    step['screenshot'] = ''
    step['stepExpectResult'] = ''
    step['eclapse'] = ''
    step['caseLogId'] = 'generate in db'
    step['stepId'] = 'step'<<i.to_s
    step['stepDesc'] = ''
    step['endTime'] = Time.new
    step['log'] = 'steplog'
    step['stepNam'] = i
    steplog<<step
  end

  log<<{'caseLog'=>caselog, 'stepLog'=>steplog}
end

result = File.new(exedata['result'], 'w+:utf-8')
result<<JSON.pretty_generate(log)
result.close
