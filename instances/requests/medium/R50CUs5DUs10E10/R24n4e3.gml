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
    ant 8
    prb 4
    x 16
    y 103
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 3
    x 106
    y 73
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 9
    prb 5
    x 75
    y 101
  ]
  edge [
    source 0
    target 2
    bandwith 620
    delay 335
  ]
  edge [
    source 0
    target 1
    bandwith 450
    delay 331
  ]
  edge [
    source 0
    target 3
    bandwith 855
    delay 164
  ]
]
