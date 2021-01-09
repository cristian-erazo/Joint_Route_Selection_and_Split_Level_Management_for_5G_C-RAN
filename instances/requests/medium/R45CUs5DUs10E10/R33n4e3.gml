graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 1
    prc 3
    ant 10
    prb 3
    x 14
    y 86
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 10
    prb 5
    x 69
    y 87
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 6
    prb 4
    x 35
    y 96
  ]
  edge [
    source 0
    target 1
    bandwith 172
    delay 238
  ]
  edge [
    source 0
    target 2
    bandwith 863
    delay 288
  ]
  edge [
    source 0
    target 3
    bandwith 595
    delay 303
  ]
]
