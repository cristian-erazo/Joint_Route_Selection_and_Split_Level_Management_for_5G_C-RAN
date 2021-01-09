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
    prc 5
    ant 4
    prb 5
    x 49
    y 22
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 9
    prb 2
    x 95
    y 72
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 10
    prb 6
    x 59
    y 41
  ]
  edge [
    source 0
    target 3
    bandwith 305
    delay 105
  ]
  edge [
    source 0
    target 2
    bandwith 877
    delay 461
  ]
  edge [
    source 0
    target 1
    bandwith 839
    delay 360
  ]
]
